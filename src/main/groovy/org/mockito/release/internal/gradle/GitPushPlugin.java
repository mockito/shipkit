package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.Exec;
import org.mockito.release.gradle.ReleaseConfiguration;
import org.mockito.release.gradle.SecureExecTask;
import org.mockito.release.internal.gradle.util.GitUtil;
import org.mockito.release.internal.gradle.util.TaskMaker;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.release.internal.gradle.configuration.DeferredConfiguration.deferredConfiguration;
import static org.mockito.release.internal.gradle.configuration.LazyConfiguration.lazyConfiguration;

public class GitPushPlugin implements Plugin<Project> {

    static final String GIT_TAG_TASK = "gitTag";
    static final String GIT_PUSH_TASK = "gitPush";
    static final String PERFORM_GIT_PUSH_TASK = "performGitPush";
    static final String GIT_COMMIT_TASK = "gitCommit";

    @Override
    public void apply(final Project project) {
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();
        final GitStatusPlugin.GitStatus gitStatus = project.getPlugins().apply(GitStatusPlugin.class).getGitStatus();

        TaskMaker.task(project, GIT_COMMIT_TASK, GitCommitTask.class, new Action<GitCommitTask>() {
            public void execute(final GitCommitTask t) {
                t.setDescription("Commits all changed files using generic --author and aggregated commit message");
                t.doFirst(new Action<Task>() {
                    @Override
                    public void execute(Task task) {
                        List<Object> arguments = new ArrayList<Object>();
                        arguments.add("git");
                        arguments.add("commit");
                        arguments.add("--author");
                        arguments.add(GitUtil.getGitGenericUserNotation(conf));
                        arguments.add("-m");
                        arguments.add(GitUtil.getCommitMessage(conf, t.getAggregatedMessage()));
                        arguments.addAll(t.getFiles());

                        t.commandLine(arguments);
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

        TaskMaker.task(project, GIT_PUSH_TASK, SecureExecTask.class, new Action<SecureExecTask>() {
            public void execute(final SecureExecTask t) {
                t.setDescription("Pushes automatically created commits to remote repo.");
                t.mustRunAfter(GIT_COMMIT_TASK);
                t.mustRunAfter(GIT_TAG_TASK);

                lazyConfiguration(t, new Runnable() {
                    public void run() {
                        t.setCommandLine(GitUtil.getGitPushArgs(conf, gitStatus.getBranch()));
                        t.setSecretValue(conf.getGitHub().getWriteAuthToken());
                    }
                });
            }
        });

        TaskMaker.task(project, PERFORM_GIT_PUSH_TASK, Task.class, new Action<Task>() {
            public void execute(final Task t) {
                t.setDescription("Pushes automatically created commits to remote repo.");
                t.dependsOn(GIT_COMMIT_TASK, GIT_PUSH_TASK);
            }
        });
    }
}
