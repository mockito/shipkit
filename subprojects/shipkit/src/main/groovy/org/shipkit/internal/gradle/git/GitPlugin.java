package org.shipkit.internal.gradle.git;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.gradle.exec.ShipkitExecTask;
import org.shipkit.gradle.git.GitCommitTask;
import org.shipkit.gradle.git.GitPushTask;
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin;
import org.shipkit.internal.gradle.util.GitUtil;
import org.shipkit.internal.gradle.util.TaskMaker;

import java.io.File;
import java.util.List;

import static org.shipkit.internal.gradle.exec.ExecCommandFactory.execCommand;
import static org.shipkit.internal.gradle.util.GitUtil.getTag;

/**
 * Adds Git-specific tasks needed for the release process.
 *
 * Applies plugins:
 * <ul>
 *     <li>{@link ShipkitConfigurationPlugin}</li>
 *     <li>{@link GitBranchPlugin}</li>
 * </ul>
 *
 * Adds tasks:
 * <ul>
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

    public static final String PERFORM_GIT_COMMIT_CLEANUP_TASK = "performGitCommitCleanUp";
    static final String GIT_STASH_TASK = "gitStash";
    static final String SOFT_RESET_COMMIT_TASK = "gitSoftResetCommit";
    public static final String TAG_CLEANUP_TASK = "gitTagCleanUp";
    static final String GIT_TAG_TASK = "gitTag";
    public static final String GIT_PUSH_TASK = "gitPush";
    public static final String PERFORM_GIT_PUSH_TASK = "performGitPush";
    public static final String GIT_COMMIT_TASK = "gitCommit";

    public void apply(final Project project) {
        final ShipkitConfiguration conf = project.getPlugins().apply(ShipkitConfigurationPlugin.class).getConfiguration();

        GitCommitTaskFactory.createGitCommitTask(project, GIT_COMMIT_TASK,
            "Commits all changed files using generic --author and aggregated commit message");

        TaskMaker.task(project, GIT_TAG_TASK, ShipkitExecTask.class, new Action<ShipkitExecTask>() {
            public void execute(final ShipkitExecTask t) {
                t.mustRunAfter(GIT_COMMIT_TASK);
                final String tag = GitUtil.getTag(conf, project);
                t.setDescription("Creates new version tag '" + tag + "'");
                t.execCommand(execCommand("Creating tag",
                    "git", "tag", "-a", tag, "-m", GitUtil.getCommitMessage("Created new tag " + tag, conf.getGit().getCommitMessagePostfix())));
            }
        });

        TaskMaker.task(project, GIT_PUSH_TASK, GitPushTask.class, new Action<GitPushTask>() {
            public void execute(final GitPushTask t) {
                t.setDescription("Pushes automatically created commits to remote repo.");
                t.mustRunAfter(GIT_COMMIT_TASK);
                t.mustRunAfter(GIT_TAG_TASK);
                t.dependsOn(GitBranchPlugin.IDENTIFY_GIT_BRANCH);
                t.getTargets().add(GitUtil.getTag(conf, project));
                t.setDryRun(conf.isDryRun());

                GitUrlInfo info = new GitUrlInfo(conf);
                t.setUrl(info.getGitUrl());
                t.setSecretValue(info.getWriteToken());

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

        TaskMaker.task(project, GIT_STASH_TASK, ShipkitExecTask.class, new Action<ShipkitExecTask>() {
            public void execute(final ShipkitExecTask t) {
                t.setDescription("Stashes current changes");
                t.execCommand(execCommand("Stashing changes", "git", "stash"));
                t.mustRunAfter(SOFT_RESET_COMMIT_TASK);
            }
        });

        TaskMaker.task(project, SOFT_RESET_COMMIT_TASK, ShipkitExecTask.class, new Action<ShipkitExecTask>() {
            public void execute(final ShipkitExecTask t) {
                t.setDescription("Removes last commit, using 'reset --soft HEAD~'");
                t.execCommand(execCommand("Removing last commit", "git", "reset", "--soft", "HEAD~"));
            }
        });

        TaskMaker.task(project, TAG_CLEANUP_TASK, ShipkitExecTask.class, new Action<ShipkitExecTask>() {
            public void execute(final ShipkitExecTask t) {
                t.setDescription("Deletes version tag '" + getTag(conf, project) + "'");
                t.execCommand(execCommand("Deleting version tag", "git", "tag", "-d", getTag(conf, project)));
                t.mustRunAfter(performPush);
            }
        });
    }

    public static void registerChangesForCommitIfApplied(final List<File> changedFiles,
                                                         final String changeDescription, final Task changingTask) {
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
