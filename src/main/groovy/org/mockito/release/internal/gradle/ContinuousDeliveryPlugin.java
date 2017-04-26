package org.mockito.release.internal.gradle;

import com.jfrog.bintray.gradle.BintrayExtension;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Exec;
import org.mockito.release.gradle.BumpVersionFileTask;
import org.mockito.release.gradle.IncrementalReleaseNotes;
import org.mockito.release.gradle.ReleaseConfiguration;
import org.mockito.release.gradle.ReleaseNeededTask;
import org.mockito.release.internal.comparison.PublicationsComparatorTask;
import org.mockito.release.internal.gradle.configuration.LazyConfiguration;
import org.mockito.release.internal.gradle.util.TaskMaker;
import org.mockito.release.version.VersionInfo;

import static org.mockito.release.internal.gradle.BaseJavaLibraryPlugin.POM_TASK;
import static org.mockito.release.internal.gradle.configuration.DeferredConfiguration.deferredConfiguration;
import static org.mockito.release.internal.gradle.util.Specs.withName;
/**
 * Opinionated continuous delivery plugin.
 * Applies following plugins and preconfigures tasks provided by those plugins:
 *
 * <ul>
 *     <li>{@link ReleaseNotesPlugin}</li>
 *     <li>{@link VersioningPlugin}</li>
 *     <li>{@link GitPlugin}</li>
 * </ul>
 *
 * Adds following tasks:
 *
 * <ul>
 *     <li>gitAddBumpVersion</li>
 *     <li>TODO document all</li>
 * </ul>
 */
public class ContinuousDeliveryPlugin implements Plugin<Project> {

    private static final Logger LOG = Logging.getLogger(ContinuousDeliveryPlugin.class);

    public void apply(final Project project) {
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();

        project.getPlugins().apply(ReleaseNotesPlugin.class);
        project.getPlugins().apply(VersioningPlugin.class);
        project.getPlugins().apply(GitPlugin.class);
        project.getPlugins().apply(ContributorsPlugin.class);

        project.allprojects(new Action<Project>() {
            @Override
            public void execute(final Project subproject) {
                subproject.getPlugins().withType(BaseJavaLibraryPlugin.class, new Action<BaseJavaLibraryPlugin>() {
                    @Override
                    public void execute(BaseJavaLibraryPlugin p) {
                        final Task fetcher = project.getTasks().getByName(ContributorsPlugin.FETCH_CONTRIBUTORS_TASK);
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

        final boolean notableRelease = project.getExtensions().getByType(VersionInfo.class).isNotableRelease();

        //TODO use constants for all task names
        ((BumpVersionFileTask) project.getTasks().getByName("bumpVersionFile"))
                .setUpdateNotableVersions(notableRelease);

        //TODO we should have tasks from the same plugin to have the same group
        //let's have a task maker instance in a plugin that has sets the group accordingly
        TaskMaker.execTask(project, "gitAddBumpVersion", new Action<Exec>() {
            public void execute(Exec t) {
                t.setDescription("Performs 'git add' for the version properties file");

                //TODO dependency/assumptions on versioning plugin (move to git plugin this and other tasks?):
                t.mustRunAfter("bumpVersionFile");
                t.commandLine("git", "add", VersioningPlugin.VERSION_FILE_NAME);
                project.getTasks().getByName(GitPlugin.COMMIT_TASK).mustRunAfter(t);
            }
        });

        TaskMaker.execTask(project, "gitAddReleaseNotes", new Action<Exec>() {
            public void execute(final Exec t) {
                t.setDescription("Performs 'git add' for the release notes file");
                t.mustRunAfter("updateReleaseNotes", "updateNotableReleaseNotes");
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
                t.dependsOn("assertReleaseNeeded");
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
                                final String bintrayRepo = bintray.getPkg().getRepo();
                                configurePublicationRepo(project, bintrayRepo);
                            }
                        });
                    }
                });
            }
        });

        TaskMaker.task(project, "performRelease", new Action<Task>() {
            public void execute(final Task t) {
                t.setDescription("Performs release. " +
                        "Ship with: './gradlew performRelease -Preleasing.dryRun=false'. " +
                        "Test with: './gradlew performRelease'");

                t.dependsOn("bumpVersionFile", "updateReleaseNotes", "updateNotableReleaseNotes");
                t.dependsOn("gitAddBumpVersion", "gitAddReleaseNotes", GitPlugin.COMMIT_TASK, GitPlugin.TAG_TASK);
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

        TaskMaker.task(project, "travisReleasePrepare", new Action<Task>() {
            public void execute(Task t) {
                t.setDescription("Prepares the working copy for releasing using Travis CI");
                t.dependsOn(GitPlugin.UNSHALLOW_TASK, GitPlugin.CHECKOUT_BRANCH_TASK, GitPlugin.SET_USER_TASK, GitPlugin.SET_EMAIL_TASK);
            }
        });

        TaskMaker.task(project, "assertReleaseNeeded", ReleaseNeededTask.class, new Action<ReleaseNeededTask>() {
            public void execute(final ReleaseNeededTask t) {
                t.setDescription("Asserts that criteria for the release are met and throws exception if release not needed.");
                t.setReleasableBranchRegex(conf.getGit().getReleasableBranchRegex());


                project.allprojects(new Action<Project>() {
                    public void execute(final Project project) {
                        project.getPlugins().withType(BaseJavaLibraryPlugin.class, new Action<BaseJavaLibraryPlugin>() {
                            public void execute(BaseJavaLibraryPlugin baseJavaLibraryPlugin) {
                                // make this task depend on all comparePublications tasks
                                Task task = project.getTasks().getByName(BaseJavaLibraryPlugin.COMPARE_PUBLICATIONS_TASK);
                                t.dependsOn(task);
                            }
                        });
                    }
                });

                t.doFirst(new Action<Task>() {
                    @Override
                    public void execute(Task task) {
                        // set allPublicationsEqual basing on results of comparisons from all projects that publish artifacts
                        t.setAllPublicationsEqual(true);
                        project.allprojects(new Action<Project>() {
                            public void execute(final Project project) {
                                project.getPlugins().withType(BaseJavaLibraryPlugin.class, new Action<BaseJavaLibraryPlugin>() {
                                    public void execute(BaseJavaLibraryPlugin baseJavaLibraryPlugin) {
                                        PublicationsComparatorTask task = (PublicationsComparatorTask) project.getTasks().getByName(BaseJavaLibraryPlugin.COMPARE_PUBLICATIONS_TASK);
                                        boolean allPublicationsEqual = t.isAllPublicationsEqual() && task.isPublicationsEqual();
                                        t.setAllPublicationsEqual(allPublicationsEqual);
                                    }
                                });
                            }
                        });
                    }
                });

                LazyConfiguration.lazyConfiguration(t, new Runnable() {
                    public void run() {
                        t.setBranch(conf.getGit().getBranch());
                    }
                });
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
