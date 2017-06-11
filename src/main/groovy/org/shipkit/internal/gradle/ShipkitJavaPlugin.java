package org.shipkit.internal.gradle;

import com.jfrog.bintray.gradle.BintrayExtension;
import org.gradle.api.*;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.UpdateReleaseNotesTask;
import org.shipkit.gradle.ReleaseConfiguration;
import org.shipkit.internal.gradle.util.BintrayUtil;
import org.shipkit.internal.gradle.util.TaskMaker;

import static org.shipkit.internal.gradle.BaseJavaLibraryPlugin.MAVEN_LOCAL_TASK;
import static org.shipkit.internal.gradle.BaseJavaLibraryPlugin.POM_TASK;
import static org.shipkit.internal.gradle.ContributorsPlugin.FETCH_ALL_CONTRIBUTORS_TASK;
import static org.shipkit.internal.gradle.configuration.DeferredConfiguration.deferredConfiguration;
import static org.shipkit.internal.gradle.configuration.LazyConfiguration.lazyConfiguration;
import static org.shipkit.internal.gradle.util.Specs.withName;
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
public class ShipkitJavaPlugin implements Plugin<Project> {

    private static final Logger LOG = Logging.getLogger(ShipkitJavaPlugin.class);

    public void apply(final Project project) {
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();

        //TODO ShipkitJavaPlugin should have no code but only apply other plugins
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
                if (conf.isPublishAllJavaSubprojects()) {
                    subproject.getPlugins().withId("java", new Action<Plugin>() {
                        @Override
                        public void execute(Plugin plugin) {
                            subproject.getPlugins().apply(JavaLibraryPlugin.class);
                        }
                    });
                }

                subproject.getPlugins().withType(BaseJavaLibraryPlugin.class, new Action<BaseJavaLibraryPlugin>() {
                    @Override
                    public void execute(BaseJavaLibraryPlugin p) {
                        final Task fetcher = project.getTasks().getByName(FETCH_ALL_CONTRIBUTORS_TASK);
                        //Because maven-publish plugin uses new configuration model, we cannot get the task directly
                        //So we use 'matching' technique
                        subproject.getTasks().matching(withName(POM_TASK)).all(new Action<Task>() {
                            public void execute(Task t) {
                                t.dependsOn(fetcher);
                            }
                        });
                    }
                });
            }
        });

        final Task performRelease = TaskMaker.task(project, "performRelease", new Action<Task>() {
            public void execute(final Task t) {
                t.setDescription("Performs release. " +
                        "Ship with: './gradlew performRelease -Pshipkit.dryRun=false'. " +
                        "Test with: './gradlew testRelease'");

                t.dependsOn(VersioningPlugin.BUMP_VERSION_FILE_TASK, "updateReleaseNotes");
                t.dependsOn(GitPlugin.PERFORM_GIT_PUSH_TASK);

                project.getTasks().getByName(GitPlugin.PERFORM_GIT_COMMIT_CLEANUP_TASK).mustRunAfter(t);
                project.getTasks().getByName(GitPlugin.TAG_CLEANUP_TASK).mustRunAfter(t);
            }
        });

        project.allprojects(new Action<Project>() {
            public void execute(final Project subproject) {
                subproject.getPlugins().withType(JavaLibraryPlugin.class, new Action<JavaLibraryPlugin>() {
                    public void execute(JavaLibraryPlugin plugin) {
                        Task bintrayUpload = subproject.getTasks().getByName(BintrayPlugin.BINTRAY_UPLOAD_TASK);
                        performRelease.dependsOn(bintrayUpload);

                        //Making git push run as late as possible because it is an operation that is hard to reverse.
                        //Git push will be executed after all tasks needed by bintrayUpload
                        // but before bintrayUpload.
                        //Using task path as String because the task comes from maven-publish new configuration model
                        // and we cannot refer to it in a normal way, by task instance.
                        String mavenLocalTask = subproject.getPath() + ":" + MAVEN_LOCAL_TASK;
                        Task gitPush = project.getTasks().getByName(GitPlugin.GIT_PUSH_TASK);
                        gitPush.mustRunAfter(mavenLocalTask);
                        //bintray upload after git push so that when git push fails we don't publish jars to bintray
                        //git push is easier to undo than deleting published jars (not possible with Central)
                        bintrayUpload.mustRunAfter(gitPush);

                        final BintrayExtension bintray = subproject.getExtensions().getByType(BintrayExtension.class);
                        //TODO clean up below. We don't need 'deferredConfiguration' because at this point
                        // shipkit file was already loaded and java library plugin applied on the subproject
                        deferredConfiguration(subproject, new Runnable() {
                            public void run() {
                                configurePublicationRepo(project, BintrayUtil.getRepoLink(bintray));
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
                                    " 'shipkit.dryRun' must be set to 'true'.\n" +
                                    "See Javadoc for ReleaseConfigurationPlugin.");
                        }
                    }
                });
            }
        });

        TaskMaker.task(project, "releaseCleanUp", new Action<Task>() {
            public void execute(final Task t) {
                t.setDescription("Cleans up the working copy, useful after dry running the release");

                //using finalizedBy so that all clean up tasks run, even if one of them fails
                t.finalizedBy(GitPlugin.PERFORM_GIT_COMMIT_CLEANUP_TASK);
                t.finalizedBy(GitPlugin.TAG_CLEANUP_TASK);
            }
        });
    }

    private static void configurePublicationRepo(Project project, String bintrayRepo) {
        //not using 'getTasks().withType()' because I don't want to create too many task configuration rules
        //TODO add information about it in the development guide
        for (Task t : project.getTasks()) {
            if (t instanceof UpdateReleaseNotesTask) {
                UpdateReleaseNotesTask task = (UpdateReleaseNotesTask) t;
                if (task.getPublicationRepository() == null) {
                    LOG.info("Configuring publication repository '{}' on task: {}", bintrayRepo, t.getPath());
                    task.setPublicationRepository(bintrayRepo);
                }
            }
        }
        //TODO unit test coverage
    }
}
