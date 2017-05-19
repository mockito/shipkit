package org.mockito.release.internal.gradle;

import com.jfrog.bintray.gradle.BintrayExtension;
import org.gradle.api.*;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Exec;
import org.mockito.release.gradle.BumpVersionFileTask;
import org.mockito.release.gradle.IncrementalReleaseNotes;
import org.mockito.release.gradle.ReleaseConfiguration;
import org.mockito.release.gradle.ReleaseNeededTask;
import org.mockito.release.internal.comparison.PublicationsComparatorTask;
import org.mockito.release.internal.gradle.util.BintrayUtil;
import org.mockito.release.internal.gradle.util.TaskMaker;
import org.mockito.release.version.VersionInfo;

import static org.mockito.release.internal.gradle.BaseJavaLibraryPlugin.POM_TASK;
import static org.mockito.release.internal.gradle.configuration.DeferredConfiguration.deferredConfiguration;
import static org.mockito.release.internal.gradle.configuration.LazyConfiguration.lazyConfiguration;
import static org.mockito.release.internal.gradle.util.Specs.withName;
/**
 * Opinionated continuous delivery plugin.
 * Applies following plugins and preconfigures tasks provided by those plugins:
 *
 * <ul>
 *     <li>{@link ReleaseNotesPlugin}</li>
 *     <li>{@link VersioningPlugin}</li>
 *     <li>{@link GitPlugin}</li>
 *     <li>{@link ContributorsPlugin}</li>
 *     <li>{@link TravisPlugin}</li>
 * </ul>
 *
 * Adds following tasks:
 *
 * <ul>
 *     <li>TODO document all</li>
 * </ul>
 */
public class ContinuousDeliveryPlugin implements Plugin<Project> {

    private static final Logger LOG = Logging.getLogger(ContinuousDeliveryPlugin.class);

    public void apply(final Project project) {
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();

        //TODO ContinuousDeliveryPlugin should have no code but only apply other plugins
        //This way it will be easy for others to put together setup for other tools / build systems

        project.getPlugins().apply(ReleaseNotesPlugin.class);
        project.getPlugins().apply(AutoVersioningPlugin.class);
        project.getPlugins().apply(GitPlugin.class);
        project.getPlugins().apply(ContributorsPlugin.class);
        project.getPlugins().apply(TravisPlugin.class);
        project.getPlugins().apply(ReleaseNeededPlugin.class);

        project.allprojects(new Action<Project>() {
            @Override
            public void execute(final Project subproject) {
                subproject.getPlugins().withType(BaseJavaLibraryPlugin.class, new Action<BaseJavaLibraryPlugin>() {
                    @Override
                    public void execute(BaseJavaLibraryPlugin p) {
                        final Task contributors = project.getTasks().getByName(ContributorsPlugin.CONFIGURE_CONTRIBUTORS_TASK);
                        //Because maven-publish plugin uses new configuration model, we cannot get the task directly
                        //So we use 'matching' technique
                        subproject.getTasks().matching(withName(POM_TASK)).all(new Action<Task>() {
                            public void execute(Task t) {
                                t.dependsOn(contributors);
                            }
                        });
                    }
                });
            }
        });

        final boolean notableRelease = project.getExtensions().getByType(VersionInfo.class).isNotableRelease();

        //TODO use constants for all task names
        ((BumpVersionFileTask) project.getTasks().getByName(VersioningPlugin.BUMP_VERSION_FILE_TASK))
                .setUpdateNotableVersions(notableRelease);

        //TODO we should have tasks from the same plugin to have the same group
        //let's have a task maker instance in a plugin that has sets the group accordingly

        TaskMaker.execTask(project, "gitAddReleaseNotes", new Action<Exec>() {
            public void execute(final Exec t) {
                t.setDescription("Performs 'git add' for the release notes file");
                t.mustRunAfter("updateReleaseNotes");
                project.getTasks().getByName(GitPlugin.COMMIT_TASK).mustRunAfter(t);
                t.doFirst(new Action<Task>() {
                    public void execute(Task task) {
                        //doFirst (execution time)
                        // so that we can access user-configured properties
                        t.commandLine("git", "add", conf.getReleaseNotes().getFile(), conf.getReleaseNotes().getNotableFile());
                    }
                });
            }
        });

        final Task bintrayUploadAll = TaskMaker.task(project, "bintrayUploadAll", new Action<Task>() {
            public void execute(Task t) {
                t.setDescription("Depends on all 'bintrayUpload' tasks from all Gradle projects.");
                //It is safer to run bintray upload after git push (hard to reverse operation)
                //This way, when git push fails we don't publish jars to bintray
                t.mustRunAfter(GitPlugin.PUSH_TASK);
            }
        });
        //TODO can we make git push and bintray upload tasks to be last (expensive, hard to reverse tasks should go last)

        project.allprojects(new Action<Project>() {
            public void execute(final Project p) {
                p.getPlugins().withType(BintrayPlugin.class, new Action<BintrayPlugin>() {
                    public void execute(BintrayPlugin bintrayPlugin) {
                        Task bintrayUpload = p.getTasks().getByName(BintrayPlugin.BINTRAY_UPLOAD_TASK);
                        bintrayUploadAll.dependsOn(bintrayUpload);
                        final BintrayExtension bintray = p.getExtensions().getByType(BintrayExtension.class);

                        deferredConfiguration(p, new Runnable() {
                            public void run() {
                                configurePublicationRepo(project, BintrayUtil.getMarkdownRepoLink(bintray));
                            }
                        });
                    }
                });
            }
        });

        TaskMaker.task(project, "testRelease", new Action<Task>() {
            @Override
            public void execute(final Task t) {
                t.setDescription("Tests the release procedure and cleans up. Safe to be invoked multiple times.");
                //releaseCleanUp is already set up to run all his "subtasks" after performRelease is performed
                //releaseNeeded is used here only to execute the code paths in the release needed task (extra testing)
                t.dependsOn("releaseNeeded", "performRelease", "releaseCleanUp");

                //Ensure that when 'testRelease' is invoked we must be using 'dryRun'
                //This is to avoid unintentional releases during testing
                lazyConfiguration(t, new Runnable() {
                    public void run() {
                        if (!conf.isDryRun()) {
                            throw new GradleException("When '" + t.getName() + "' task is executed" +
                                    " 'releasing.dryRun' must be set to 'true'.\n" +
                                    "See Javadoc for ReleaseConfigurationPlugin.");
                        }
                    }
                });
            }
        });

        //commit task that is added by GitPlugin needs to run after the add bump version task added by AutoVersioningPlugin
        //TODO: let's think about a way to avoid 'git add' tasks completely.
        //      We can use git commit with paths to commit specific files without the need for 'add' operation
        //      Using 'git add' adds complexity and causes weird bugs like #145
        project.getTasks().getByName(GitPlugin.COMMIT_TASK).mustRunAfter(AutoVersioningPlugin.ADD_BUMP_VERSION_TASK);

        TaskMaker.task(project, "performRelease", new Action<Task>() {
            public void execute(final Task t) {
                t.setDescription("Performs release. " +
                        "Ship with: './gradlew performRelease -Preleasing.dryRun=false'. " +
                        "Test with: './gradlew testRelease'");

                t.dependsOn(VersioningPlugin.BUMP_VERSION_FILE_TASK, "updateReleaseNotes");
                t.dependsOn(AutoVersioningPlugin.ADD_BUMP_VERSION_TASK, "gitAddReleaseNotes", GitPlugin.COMMIT_TASK, GitPlugin.TAG_TASK);
                t.dependsOn(GitPlugin.PUSH_TASK);
                t.dependsOn("bintrayUploadAll");

                project.getTasks().getByName(GitPlugin.COMMIT_CLEANUP_TASK).mustRunAfter(t);
                project.getTasks().getByName(GitPlugin.TAG_CLEANUP_TASK).mustRunAfter(t);
            }
        });

        TaskMaker.task(project, "releaseCleanUp", new Action<Task>() {
            public void execute(final Task t) {
                t.setDescription("Cleans up the working copy, useful after dry running the release");

                //using finalizedBy so that all clean up tasks run, even if one of them fails
                t.finalizedBy(GitPlugin.COMMIT_CLEANUP_TASK);
                t.finalizedBy(GitPlugin.TAG_CLEANUP_TASK);
            }
        });
    }

    private static void configurePublicationRepo(Project project, String bintrayRepo) {
        //not using 'getTasks().withType()' because I don't want to create too many task configuration rules
        //TODO add information about it in the development guide
        for (Task t : project.getTasks()) {
            if (t instanceof IncrementalReleaseNotes) {
                IncrementalReleaseNotes task = (IncrementalReleaseNotes) t;
                if (task.getPublicationRepository() == null) {
                    LOG.info("Configuring publication repository '{}' on task: {}", bintrayRepo, t.getPath());
                    task.setPublicationRepository(bintrayRepo);
                }
            }
        }
        //TODO unit test coverage
    }
}
