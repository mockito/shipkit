package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.publish.maven.tasks.GenerateMavenPom;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.Exec;
import org.gradle.process.ExecResult;
import org.gradle.process.ExecSpec;
import org.mockito.release.gradle.BumpVersionFileTask;
import org.mockito.release.gradle.ReleaseConfiguration;
import org.mockito.release.internal.gradle.util.StringUtil;
import org.mockito.release.internal.gradle.util.TaskMaker;
import org.mockito.release.version.VersionInfo;

import static java.util.Arrays.asList;

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
                        subproject.getTasks().withType(GenerateMavenPom.class, new Action<GenerateMavenPom>() {
                            @Override
                            public void execute(GenerateMavenPom generateMavenPom) {
                                generateMavenPom.dependsOn(fetcher);
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

        configureNotableReleaseNotes(project, notableRelease);

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
            }
        });
        //TODO can we make git push and bintray upload tasks to be last (expensive, hard to reverse tasks should go last)

        project.allprojects(new Action<Project>() {
            public void execute(final Project project) {
                project.getPlugins().withType(BintrayPlugin.class, new Action<BintrayPlugin>() {
                    public void execute(BintrayPlugin bintrayPlugin) {
                        Task bintrayUpload = project.getTasks().getByName(BintrayPlugin.BINTRAY_UPLOAD_TASK);
                        bintrayUploadAll.dependsOn(bintrayUpload);
                    }
                });
            }
        });

        TaskMaker.task(project, "performRelease", new Action<Task>() {
            public void execute(final Task t) {
                t.setDescription("Performs release. To test release use './gradlew testRelease'");

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

        final Task releaseNeededTask = TaskMaker.task(project, "releaseNeeded", new Action<Task>() {
            public void execute(final Task t) {
                t.setDescription("Checks if the criteria for the release are met.");
                t.doLast(new Action<Task>() {
                    public void execute(Task task) {
                        //TODO hardcoded literals
                        boolean skipEnvVariable = System.getenv("SKIP_RELEASE") != null;
                        String commitMessage = System.getenv("TRAVIS_COMMIT_MESSAGE");
                        boolean skippedByCommitMessage = commitMessage != null && commitMessage.contains("[ci skip-release]");

                        //returns true only if pull request env variable points to PR number
                        String pr = System.getenv("TRAVIS_PULL_REQUEST");
                        boolean pullRequest = pr != null && !pr.trim().isEmpty() && !pr.equals("false");

                        //TODO create task that reads the current branch in case TRAVIS_BRANCH env variable is not set
                        String branch = System.getenv("TRAVIS_BRANCH");
                        boolean releasableBranch = branch != null && branch.matches(conf.getGit().getReleasableBranchRegex());

                        boolean notNeeded = skipEnvVariable || skippedByCommitMessage || pullRequest || !releasableBranch;
                        //TODO task type, otherwise 'needed' is just a String, no type safety
                        t.getExtensions().getExtraProperties().set("needed", !notNeeded);

                        LOG.lifecycle("  Release is needed: " + !notNeeded +
                                "\n    - skip by env variable: " + skipEnvVariable +
                                "\n    - skip by commit message: " + skippedByCommitMessage +
                                "\n    - is pull request build:  " + pullRequest +
                                "\n    - is releasable branch:  " + releasableBranch);
                    }
                });
            }
        });

        TaskMaker.task(project, "travisRelease", new Action<Task>() {
            public void execute(final Task t) {
                t.setDescription("Performs the release if release criteria are met, intended to be used by Travis CI job");
                t.dependsOn("releaseNeeded");
                t.onlyIf(new Spec<Task>() {
                    public boolean isSatisfiedBy(Task task) {
                        //also checking didWork so that we can "-x releaseNeeded" to force the release without criteria check
                        return releaseNeededTask.getDidWork()
                                //TODO below is awkward, we need a new task type
                                && (Boolean) releaseNeededTask.getExtensions().getExtraProperties().get("needed");
                    }
                });
                t.doLast(new Action<Task>() {
                    public void execute(Task task) {
                        LOG.lifecycle("  Performing release of version '{}' (notable version: {})", project.getVersion(), notableRelease);
                        //first, we need to prepare Travis working copy
                        exec(project, "./gradlew", "travisReleasePrepare");
                        //then, we will do the full release with dry run to flesh out any issues
                        performReleaseTest(project);
                        //finally, let's make the release!
                        exec(project, "./gradlew", "performRelease");
                    }
                });

                //TODO I think we should consider deleting 'travisRelease' task. It could be repaced by something like:
                //./gradlew assertReleaseNeeded && ./gradlew travisReleasePrepare && ./gradlew performRelease releaseCleanUp -PreleaseDryRun && ./gradlew performRelease
                //assertReleaseNeeded task would fail when release is not needed and would stop executing further commands.
            }
        });

        //TODO delete this task, instead we can just print to the user:
        // to test the release, run "./gradlew performRelease releaseCleanUp -PreleaseDryRun"
        //forking off gradle process to run tasks, from inside gradle tasks is not a good idea.
        TaskMaker.task(project, "testRelease", new Action<Task>() {
            public void execute(Task t) {
                t.setDescription("Tests the release, intended to be used locally by engineers");
                t.doLast(new Action<Task>() {
                    public void execute(Task task) {
                        performReleaseTest(project);
                    }
                });
            }
        });
    }

    private static void configureNotableReleaseNotes(Project project, boolean notableRelease) {
        VersionInfo versionInfo = project.getExtensions().getByType(VersionInfo.class);
        NotableReleaseNotesGeneratorTask generatorTask = (NotableReleaseNotesGeneratorTask) project.getTasks().getByName("updateNotableReleaseNotes");
        NotableReleaseNotesFetcherTask fetcherTask = (NotableReleaseNotesFetcherTask) project.getTasks().getByName("fetchNotableReleaseNotes");

        generatorTask.getNotesGeneration().setTargetVersions(versionInfo.getNotableVersions());
        fetcherTask.getNotesGeneration().setTargetVersions(versionInfo.getNotableVersions());

        if (notableRelease) {
            generatorTask.getNotesGeneration().setHeadVersion(project.getVersion().toString());
            fetcherTask.getNotesGeneration().setHeadVersion(project.getVersion().toString());
        }
    }

    private static void performReleaseTest(Project project) {
        exec(project, "./gradlew", "performRelease", "releaseCleanUp", "-PreleaseDryRun");
    }

    private static ExecResult exec(Project project, final String... commandLine) {
        LOG.lifecycle("  Running:\n    " + StringUtil.join(asList(commandLine), " ") );
        return project.exec(new Action<ExecSpec>() {
            public void execute(ExecSpec e) {
                e.commandLine(commandLine);
            }
        });
    }
}
