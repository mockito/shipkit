package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Exec;
import org.mockito.release.gradle.ReleaseConfiguration;
import org.mockito.release.internal.gradle.util.ExtContainer;
import org.mockito.release.internal.gradle.util.GitUtil;
import org.mockito.release.internal.gradle.util.TaskMaker;

import java.io.ByteArrayOutputStream;

import static org.mockito.release.internal.gradle.configuration.LazyConfiguration.lazyConfiguration;
import static org.mockito.release.internal.gradle.util.StringUtil.join;

/**
 * Adds Git-specific tasks needed for the release process.
 */
public class GitPlugin implements Plugin<Project> {

    private static final Logger LOG = Logging.getLogger(GitPlugin.class);

    static final String COMMIT_TASK = "gitCommit";
    static final String TAG_TASK = "gitTag";
    static final String PUSH_TASK = "gitPush";
    static final String COMMIT_CLEANUP_TASK = "gitCommitCleanUp";
    static final String TAG_CLEANUP_TASK = "gitTagCleanUp";
    static final String UNSHALLOW_TASK = "gitUnshallow";
    static final String CHECKOUT_BRANCH_TASK = "checkOutBranch";
    static final String SET_USER_TASK = "setGitUserName";
    static final String SET_EMAIL_TASK = "setGitUserEmail";

    public void apply(final Project project) {
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();

        final ExtContainer ext = new ExtContainer(project);

        TaskMaker.execTask(project, COMMIT_TASK, new Action<Exec>() {
            public void execute(final Exec t) {
                t.setDescription("Commits staged changes using generic --author");
                t.doFirst(new Action<Task>() {
                    public void execute(Task task) {
                        //doFirst (execution time) to pick up user-configured setting
                        t.commandLine("git", "commit", "--author",
                                ext.getGitGenericUserNotation(), "-m", commitMessage("Bumped version and updated release notes"));
                    }
                });
            }
        });

        TaskMaker.execTask(project, TAG_TASK, new Action<Exec>() {
            public void execute(Exec t) {
                t.mustRunAfter(COMMIT_TASK);
                String tag = "v" + project.getVersion();
                t.setDescription("Creates new version tag '" + tag + "'");
                t.commandLine("git", "tag", "-a", tag, "-m", commitMessage("Created new tag " + tag));
            }
        });

        boolean mustBeQuiet = true; //so that we don't expose the token
        TaskMaker.execTask(project, PUSH_TASK, mustBeQuiet, new Action<Exec>() {
            public void execute(final Exec t) {
                t.setDescription("Pushes changes to remote repo.");
                t.mustRunAfter(COMMIT_TASK, TAG_TASK);

                //!!!We must capture and hide the output because when git push fails it can expose the token!
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                t.setStandardOutput(output);
                t.setErrorOutput(output);

                lazyConfiguration(t, new Runnable() {
                    public void run() {
                        t.commandLine(GitUtil.getQuietGitPushArgs(conf, ext));
                    }
                });
            }
        });

        TaskMaker.execTask(project, COMMIT_CLEANUP_TASK, new Action<Exec>() {
            public void execute(final Exec t) {
                t.setDescription("Removes last commit, using 'reset --hard HEAD~'");
                //TODO replace with combination of 'git reset --soft HEAD~ && git stash' so that we don't lose commits
                t.commandLine("git", "reset", "--hard", "HEAD~");
            }
        });

        TaskMaker.execTask(project, TAG_CLEANUP_TASK, new Action<Exec>() {
            public void execute(final Exec t) {
                t.setDescription("Deletes version tag '" + ext.getTag() + "'");
                t.commandLine("git", "tag", "-d", ext.getTag());
            }
        });

        TaskMaker.execTask(project, UNSHALLOW_TASK, new Action<Exec>() {
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

        TaskMaker.execTask(project, CHECKOUT_BRANCH_TASK, new Action<Exec>() {
            public void execute(final Exec t) {
                t.setDescription("Checks out the branch that can be committed. CI systems often check out revision that is not committable.");
                lazyConfiguration(t, new Runnable() {
                    public void run() {
                        t.commandLine("git", "checkout", conf.getGit().getBranch());
                    }
                });
            }
        });

        TaskMaker.execTask(project, SET_USER_TASK, new Action<Exec>() {
            public void execute(final Exec t) {
                t.setDescription("Overwrites local git 'user.name' with a generic name. Intended for CI.");
                //TODO replace all doFirst in this class with LazyConfiguration
                t.doFirst(new Action<Task>() {
                    public void execute(Task task) {
                        //using doFirst() so that we request and validate presence of env var only during execution time
                        t.commandLine("git", "config", "--local", "user.name", ext.getGitGenericUser());
                    }
                });
            }
        });

        TaskMaker.execTask(project, SET_EMAIL_TASK, new Action<Exec>() {
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
    }

    private static String commitMessage(String message) {
        //TODO it is awkward to couple the git plugin with travis here
        //Example solution: we could create CommitMessage interface that needs to have the implementation supplied
        //The implementation will have to be set on the ext and git plugin by anyone who applies git plugin
        String buildNo = System.getenv("TRAVIS_BUILD_NUMBER");
        if (buildNo != null) {
            return message + " by Travis CI build " + buildNo + " [ci skip]";
        } else {
            return message + " [ci skip]";
        }
    }
}
