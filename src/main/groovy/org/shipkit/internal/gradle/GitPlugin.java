package org.shipkit.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Exec;
import org.shipkit.gradle.ReleaseConfiguration;
import org.shipkit.gradle.git.GitPushTask;
import org.shipkit.gradle.git.IdentifyGitBranchTask;
import org.shipkit.internal.gradle.git.GitBranchPlugin;
import org.shipkit.internal.gradle.git.GitPushArgs;
import org.shipkit.internal.gradle.util.GitUtil;
import org.shipkit.internal.gradle.util.StringUtil;
import org.shipkit.internal.gradle.util.TaskMaker;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.shipkit.internal.gradle.configuration.DeferredConfiguration.deferredConfiguration;
import static org.shipkit.internal.gradle.util.GitUtil.getTag;

/**
 * Adds Git-specific tasks needed for the release process:
 *
 * <ul>
 *     <li>identifyGitBranch - {@link IdentifyGitBranchTask}</li>
 *     <li>gitCommit</li>
 *     <li>gitTag</li>
 *     <li>gitPush</li>
 *     <li>performGitPush - {@link GitPushTask}</li>
 *
 *     <li>performGitCommitCleanUp</li>
 *     <li>gitSoftResetCommit</li>
 *     <li>gitStash</li>
 *     <li>gitTagCleanUp</li>
 * </ul>
 */
public class GitPlugin implements Plugin<Project> {

    static final String PERFORM_GIT_COMMIT_CLEANUP_TASK = "performGitCommitCleanUp";
    static final String IDENTIFY_GIT_BRANCH = "identifyGitBranch";
    static final String GIT_STASH_TASK = "gitStash";
    static final String SOFT_RESET_COMMIT_TASK = "gitSoftResetCommit";
    static final String TAG_CLEANUP_TASK = "gitTagCleanUp";
    static final String GIT_TAG_TASK = "gitTag";
    static final String GIT_PUSH_TASK = "gitPush";
    static final String PERFORM_GIT_PUSH_TASK = "performGitPush";
    static final String WRITE_TOKEN_ENV = "GH_WRITE_TOKEN";
    public static final String GIT_COMMIT_TASK = "gitCommit";

    private final static Logger LOG = Logging.getLogger(GitPlugin.class);

    public void apply(final Project project) {
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();

        TaskMaker.task(project, GIT_COMMIT_TASK, GitCommitTask.class, new Action<GitCommitTask>() {
            public void execute(final GitCommitTask t) {
                t.setDescription("Commits all changed files using generic --author and aggregated commit message");
                t.doFirst(new Action<Task>() {
                    @Override
                    public void execute(Task task) {
                        Collection<String> args = new LinkedList<String>();
                        args.add("git");
                        args.add("commit");
                        args.add("--author");
                        args.add(GitUtil.getGitGenericUserNotation(conf));
                        args.add("-m");
                        args.add(GitUtil.getCommitMessage(conf, t.getAggregatedCommitMessage()));
                        args.addAll(t.getFiles());

                        LOG.lifecycle("{} - running command:\n  {}", task.getPath(), StringUtil.join(args, " "));
                        t.commandLine(args);
                    }
                });
            }
        });

        TaskMaker.execTask(project, GIT_TAG_TASK, new Action<Exec>() {
            public void execute(final Exec t) {
                t.mustRunAfter(GIT_COMMIT_TASK);
                final String tag = GitUtil.getTag(conf, project);
                t.setDescription("Creates new version tag '" + tag + "'");
                deferredConfiguration(project, new Runnable() {
                    @Override
                    public void run() {
                        t.commandLine("git", "tag", "-a", tag, "-m",
                                GitUtil.getCommitMessage(conf, "Created new tag " + tag));
                    }
                });
            }
        });

        TaskMaker.task(project, GIT_PUSH_TASK, GitPushTask.class, new Action<GitPushTask>() {
            public void execute(final GitPushTask t) {
                t.setDescription("Pushes automatically created commits to remote repo.");
                t.mustRunAfter(GIT_COMMIT_TASK);
                t.mustRunAfter(GIT_TAG_TASK);
                t.dependsOn(IDENTIFY_GIT_BRANCH);
                t.getTargets().add(GitUtil.getTag(conf, project));
                t.setDryRun(conf.isDryRun());

                GitPushArgs.setPushUrl(t, conf, System.getenv(WRITE_TOKEN_ENV));

                project.getPlugins().apply(GitBranchPlugin.class)
                        .provideBranchTo(t, new Action<String>() {
                            @Override
                            public void execute(String branch) {
                                t.getTargets().add(branch);
                            }
                        });
            }
        });

        final Task performPush = TaskMaker.task(project, PERFORM_GIT_PUSH_TASK, Task.class, new Action<Task>() {
            public void execute(final Task t) {
                t.setDescription("Performs gitCommit, gitTag and gitPush tasks and all tasks dependent on them.");
                t.dependsOn(GIT_COMMIT_TASK, GIT_TAG_TASK, GIT_PUSH_TASK);
            }
        });

        TaskMaker.task(project, PERFORM_GIT_COMMIT_CLEANUP_TASK, Task.class, new Action<Task>() {
            public void execute(final Task t) {
                t.setDescription("Performs " + SOFT_RESET_COMMIT_TASK + " and " + GIT_STASH_TASK + " tasks.");
                t.dependsOn(SOFT_RESET_COMMIT_TASK, GIT_STASH_TASK);
                t.mustRunAfter(performPush);
            }
        });

        TaskMaker.execTask(project, GIT_STASH_TASK, new Action<Exec>() {
            public void execute(final Exec t) {
                t.setDescription("Stashes current changes");
                t.commandLine("git", "stash");
                t.mustRunAfter(SOFT_RESET_COMMIT_TASK);
            }
        });

        TaskMaker.execTask(project, SOFT_RESET_COMMIT_TASK, new Action<Exec>() {
            public void execute(final Exec t) {
                t.setDescription("Removes last commit, using 'reset --soft HEAD~'");
                t.commandLine("git", "reset", "--soft", "HEAD~");
            }
        });

        TaskMaker.execTask(project, TAG_CLEANUP_TASK, new Action<Exec>() {
            public void execute(final Exec t) {
                t.setDescription("Deletes version tag '" + getTag(conf, project) + "'");
                t.commandLine("git", "tag", "-d", getTag(conf, project));
                t.mustRunAfter(performPush);
            }
        });
    }

    public static void registerChangesForCommitIfApplied(final List<File> changedFiles,
                                                         final String changeDescription, final Task changingTask){
        final Project project = changingTask.getProject();
        project.getPlugins().withType(GitPlugin.class, new Action<GitPlugin>() {
            @Override
            public void execute(GitPlugin gitPushPlugin) {
                GitCommitTask gitCommitTask = (GitCommitTask) project.getTasks().findByName(GitPlugin.GIT_COMMIT_TASK);
                gitCommitTask.addChange(changedFiles, changeDescription, changingTask);
            }
        });
    }
}
