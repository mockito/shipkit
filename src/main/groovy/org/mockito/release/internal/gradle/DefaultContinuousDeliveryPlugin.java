package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.Exec;
import org.gradle.process.ExecResult;
import org.gradle.process.ExecSpec;
import org.mockito.release.gradle.BumpVersionFileTask;
import org.mockito.release.gradle.ContinuousDeliveryPlugin;
import org.mockito.release.internal.gradle.util.CommonSettings;
import org.mockito.release.internal.gradle.util.ExtContainer;
import org.mockito.release.internal.gradle.util.LazyConfigurer;
import org.mockito.release.internal.gradle.util.StringUtil;
import org.mockito.release.version.VersionFile;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.release.internal.gradle.util.StringUtil.join;

/**
 * Please keep documentation up to date at {@link ContinuousDeliveryPlugin}
 */
public class DefaultContinuousDeliveryPlugin implements ContinuousDeliveryPlugin {

    private static final Logger LOG = Logging.getLogger(DefaultContinuousDeliveryPlugin.class);

    public void apply(final Project project) {
        project.getPlugins().apply("org.mockito.release-notes");
        project.getPlugins().apply("org.mockito.release-tools.versioning");

        final ExtContainer ext = new ExtContainer(project);

        ((BumpVersionFileTask) project.getTasks().getByName("bumpVersionFile"))
                .setUpdateNotableVersions(isNotableRelease(project));

        CommonSettings.execTask(project, "gitAddBumpVersion", new Action<Exec>() {
            public void execute(Exec t) {
                t.setDescription("Performs 'git add' for the version properties file");

                //TODO dependency/assumptions on versioning plugin (move to git plugin this and other tasks?):
                t.mustRunAfter("bumpVersionFile");
                t.commandLine("git", "add", "version.properties");
            }
        });

        configureNotableReleaseNotes(project);

        CommonSettings.execTask(project, "gitAddReleaseNotes", new Action<Exec>() {
            public void execute(final Exec t) {
                t.setDescription("Performs 'git add' for the release notes file");
                t.mustRunAfter("updateReleaseNotes", "updateNotableReleaseNotes");
                t.doFirst(new Action<Task>() {
                    public void execute(Task task) {
                        //doFirst (execution time)
                        // so that we can access user-configured properties
                        t.commandLine("git", "add", ext.getReleaseNotesFile(), ext.getNotableReleaseNotesFile());
                    }
                });
            }
        });

        CommonSettings.execTask(project, "gitCommit", new Action<Exec>() {
            public void execute(final Exec t) {
                t.setDescription("Commits staged changes using generic --author");
                t.mustRunAfter("gitAddBumpVersion", "gitAddReleaseNotes");
                t.doFirst(new Action<Task>() {
                    public void execute(Task task) {
                    //doFirst (execution time) to pick up user-configured setting
                    t.commandLine("git", "commit", "--author",
                        ext.getGitGenericUserNotation(), "-m", commitMessage("Bumped version and updated release notes"));
                    }
                });
            }
        });

        CommonSettings.execTask(project, "gitTag", new Action<Exec>() {
            public void execute(Exec t) {
                t.mustRunAfter("gitCommit");
                String tag = "v" + project.getVersion();
                t.setDescription("Creates new version tag '" + tag + "'");
                t.commandLine("git", "tag", "-a", tag, "-m", commitMessage("Created new tag " + tag));
            }
        });

        boolean mustBeQuiet = true; //so that we don't expose the token
        CommonSettings.execTask(project, "gitPush", mustBeQuiet, new Action<Exec>() {
            public void execute(final Exec t) {
                t.setDescription("Pushes changes to remote repo.");
                t.mustRunAfter("gitCommit", "gitTag");

                LazyConfigurer.getConfigurer(project).configureLazily(t, new Runnable() {
                    public void run() {
                        t.commandLine(ext.getQuietGitPushArgs());

                        //!!!We must capture and hide the output because when git push fails it can expose the token!
                        ByteArrayOutputStream output = new ByteArrayOutputStream();
                        t.setStandardOutput(output);
                        t.setErrorOutput(output);
                    }
                });
            }
        });

        final Task bintrayUploadAll = CommonSettings.task(project, "bintrayUploadAll", new Action<Task>() {
            public void execute(Task t) {
                t.setDescription("Depends on all 'bintrayUpload' tasks from all Gradle projects.");
                //It is safer to run bintray upload after git push (hard to reverse operation)
                //This way, when git push fails we don't publish jars to bintray
                t.mustRunAfter("gitPush");
            }
        });

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

        CommonSettings.task(project, "performRelease", new Action<Task>() {
            public void execute(final Task t) {
                t.setDescription("Performs release. To test release use './gradlew testRelease'");

                t.dependsOn("bumpVersionFile", "updateReleaseNotes", "updateNotableReleaseNotes");
                t.dependsOn("gitAddBumpVersion", "gitAddReleaseNotes", "gitCommit", "gitTag");
                t.dependsOn("gitPush");
                t.dependsOn("bintrayUploadAll");
            }
        });

        CommonSettings.execTask(project, "gitCommitCleanUp", new Action<Exec>() {
            public void execute(final Exec t) {
                t.setDescription("Removes last commit, using 'reset --hard HEAD~'");
                t.mustRunAfter("performRelease");
                t.commandLine("git", "reset", "--hard", "HEAD~");
            }
        });

        CommonSettings.execTask(project, "gitTagCleanUp", new Action<Exec>() {
            public void execute(final Exec t) {
                t.setDescription("Deletes version tag '" + ext.getTag() + "'");
                t.mustRunAfter("performRelease");
                t.commandLine("git", "tag", "-d", ext.getTag());
            }
        });

        CommonSettings.task(project, "releaseCleanUp", new Action<Task>() {
            public void execute(final Task t) {
                t.setDescription("Cleans up the working copy, useful after dry running the release");

                //using finalizedBy so that all clean up tasks run, even if one of them fails
                t.finalizedBy("gitCommitCleanUp");
                t.finalizedBy("gitTagCleanUp");
            }
        });

        CommonSettings.execTask(project, "gitUnshallow", new Action<Exec>() {
            public void execute(final Exec t) {
                //Travis default clone is shallow which will prevent correct release notes generation for repos with lots of commits
                t.commandLine("git", "fetch", "--unshallow");
                t.setDescription("Ensures good chunk of recent commits is available for release notes automation. Runs: " + t.getCommandLine());

                t.setIgnoreExitValue(true);
                t.doLast(new Action<Task>() {
                    public void execute(Task task) {
                        if (t.getExecResult().getExitValue() != 0) {
                            LOG.lifecycle("  Following git command failed and will be ignored:" +
                                    "\n    " + join(t.getCommandLine(), " ") +
                                    "\n  Most likely the repository already contains all history.");
                        }
                    }
                });
            }
        });

        CommonSettings.execTask(project, "checkOutBranch", new Action<Exec>() {
            public void execute(final Exec t) {
                t.setDescription("Checks out the branch that can be committed. CI systems often check out revision that is not committable.");
                LazyConfigurer.getConfigurer(project).configureLazily(t, new Runnable() {
                    public void run() {
                        t.commandLine("git", "checkout", ext.getCurrentBranch());
                    }
                });
            }
        });

        CommonSettings.execTask(project, "configureGitUserName", new Action<Exec>() {
            public void execute(final Exec t) {
                t.setDescription("Overwrites local git 'user.name' with a generic name. Intended for CI.");
                t.doFirst(new Action<Task>() {
                    public void execute(Task task) {
                        //using doFirst() so that we request and validate presence of env var only during execution time
                        t.commandLine("git", "config", "--local", "user.name", ext.getGitGenericUser());
                    }
                });
            }
        });

        CommonSettings.execTask(project, "configureGitUserEmail", new Action<Exec>() {
            public void execute(final Exec t) {
                t.setDescription("Overwrites local git 'user.email' with a generic email. Intended for CI.");
                t.doFirst(new Action<Task>() {
                    public void execute(Task task) {
                        //using doFirst() so that we request and validate presence of env var only during execution time
                        //TODO consider adding 'lazyExec' task or method that automatically uses do first
                        t.commandLine("git", "config", "--local", "user.email", ext.getGitGenericEmail());
                    }
                });
            }
        });

        CommonSettings.task(project, "travisReleasePrepare", new Action<Task>() {
            public void execute(Task t) {
                t.setDescription("Prepares the working copy for releasing using Travis CI");
                t.dependsOn("gitUnshallow", "checkOutBranch", "configureGitUserName", "configureGitUserEmail");
            }
        });

        final Task releaseNeededTask = CommonSettings.task(project, "releaseNeeded", new Action<Task>() {
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
                        boolean releasableBranch = branch != null && branch.matches(ext.getReleasableBranchRegex());

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

        CommonSettings.task(project, "travisRelease", new Action<Task>() {
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
                        //first, we need to prepare Travis working copy
                        exec(project, "./gradlew", "travisReleasePrepare");
                        //then, we will do the full release with dry run to flesh out any issues
                        performReleaseTest(project);
                        //finally, let's make the release!
                        exec(project, "./gradlew", "performRelease");
                        //perform notable release if needed
                        performNotableRelease(project, false);
                    }
                });
            }
        });

        CommonSettings.task(project, "testRelease", new Action<Task>() {
            public void execute(Task t) {
                t.setDescription("Tests the release, intended to be used locally by engineers");
                t.doLast(new Action<Task>() {
                    public void execute(Task task) {
                        performReleaseTest(project);
                    }
                });
            }
        });

        CommonSettings.task(project, "testNotableRelease", new Action<Task>() {
            public void execute(Task t) {
                t.setDescription("Tests the notable release, intended to be used locally by engineers");
                t.doLast(new Action<Task>() {
                    public void execute(Task task) {
                        performNotableRelease(project, true);
                    }
                });
            }
        });
    }

    private static void configureNotableReleaseNotes(Project project) {
        VersionFile versionFile = project.getExtensions().getByType(VersionFile.class);
        NotableReleaseNotesGeneratorTask generatorTask = (NotableReleaseNotesGeneratorTask) project.getTasks().getByName("updateNotableReleaseNotes");
        NotableReleaseNotesFetcherTask fetcherTask = (NotableReleaseNotesFetcherTask) project.getTasks().getByName("fetchNotableReleaseNotes");

        generatorTask.getNotesGeneration().setTargetVersions(versionFile.getNotableVersions());
        fetcherTask.getNotesGeneration().setTargetVersions(versionFile.getNotableVersions());

        if (isNotableRelease(project)) {
            generatorTask.getNotesGeneration().setHeadVersion(project.getVersion().toString());
            fetcherTask.getNotesGeneration().setHeadVersion(project.getVersion().toString());
        }
    }

    private static void performNotableRelease(Project project, boolean dryRun) {
        String v = project.getVersion().toString();
        if (isNotableRelease(project)) { //new minor or major version
            LOG.lifecycle("  It looks like we are releasing a new notable version '{}'!\n" +
                    "  Performing additional upload to 'notable versions' repository and Maven Central.", v);

            List<String> args = new LinkedList<String>(asList(
                    "./gradlew", "bintrayUploadAll",
                    "-Prelease_version=" + v,
                    "-Prelease_notable=true",
                    "-Pbintray_mavenCentralSync"));

            if (dryRun) {
                //TODO this is hacky we should have 'release dry run' to support 'false' as argument
                // this would clean up conditional complexity
                args.add("-PreleaseDryRun");
            }
            exec(project, args.toArray(new String[args.size()]));
        } else {
            LOG.lifecycle("  Version '{}' is not a notable version.\n" +
                    "  NO additional upload to 'notable versions' repository and NO sync to Maven Central.", v);
        }
    }

    private static boolean isNotableRelease(Project project) {
        //TODO also check for env variable here / commit message, we already check for 'TRAVIS_COMMIT_MESSAGE' elsewhere
        String v = project.getVersion().toString();
        return v.endsWith(".0") || v.endsWith(".0.0");
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

    private static String commitMessage(String message) {
        String buildNo = System.getenv("TRAVIS_BUILD_NUMBER");
        if (buildNo != null) {
            return message + " by Travis CI build " + buildNo + " [ci skip]";
        } else {
            return message + " [ci skip]";
        }
    }
}
