package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.Exec;
import org.mockito.release.internal.gradle.util.TaskMaker;

import org.mockito.release.gradle.ReleaseConfiguration;
import org.mockito.release.gradle.SecureExecTask;
import org.mockito.release.internal.gradle.util.GitUtil;

import static org.mockito.release.internal.gradle.configuration.DeferredConfiguration.deferredConfiguration;
import static org.mockito.release.internal.gradle.configuration.LazyConfiguration.lazyConfiguration;

/**
 * Plugin uses bumping version in version.properties file done by VersioningPlugin
 * and additionally commits and pushes changes to Github repo
 * You can use task "bumpVersionAndPush" to achieve all that
 *
 * Applies following plugins and preconfigures tasks provided by those plugins:
 *
 * <ul>
 *     <li>{@link VersioningPlugin}</li>
 * </ul>
 *
 * Adds following tasks:
 *
 * <ul>
 *     <li>gitAddBumpVersion</li>
 *     <li>gitCommitBumpVersion</li>
 *     <li>gitPushBumpVersion</li>
 *     <li>bumpVersionAndPush</li>
 * </ul>
 */
public class AutoVersioningPlugin implements Plugin<Project> {

    static final String BUMP_VERSION_AND_PUSH_TASK = "bumpVersionAndPush";
    static final String ADD_BUMP_VERSION_TASK = "gitAddBumpVersion";
    static final String PUSH_BUMP_VERSION_TASK = "gitPushBumpVersion";
    static final String VERSION_BUMP_COMMIT_TASK = "gitCommitBumpVersion";

    public void apply(final Project project) {
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();
        final GitStatusPlugin.GitStatus gitStatus = project.getPlugins().apply(GitStatusPlugin.class).getGitStatus();

        project.getPlugins().apply(VersioningPlugin.class);

        TaskMaker.execTask(project, VERSION_BUMP_COMMIT_TASK, new Action<Exec>() {
            public void execute(final Exec t) {
                t.setDescription("Commits bumped version file using generic --author");
                deferredConfiguration(project, new Runnable() {
                    @Override
                    public void run() {
                        t.commandLine("git", "commit", "--author",
                                GitUtil.getGitGenericUserNotation(conf), "-m",
                                GitUtil.getCommitMessage(conf, "Bumped version"));
                    }
                });
            }
        });

        TaskMaker.execTask(project, ADD_BUMP_VERSION_TASK, new Action<Exec>() {
            public void execute(Exec t) {
                t.setDescription("Performs 'git add' for the version properties file");
                t.mustRunAfter(VersioningPlugin.BUMP_VERSION_FILE_TASK);
                t.commandLine("git", "add", VersioningPlugin.VERSION_FILE_NAME);
                project.getTasks().getByName(VERSION_BUMP_COMMIT_TASK).mustRunAfter(t);
            }
        });

        TaskMaker.task(project, PUSH_BUMP_VERSION_TASK, SecureExecTask.class, new Action<SecureExecTask>() {
            public void execute(final SecureExecTask t) {
                t.setDescription("Pushes bumped version to remote repo.");
                t.mustRunAfter(VERSION_BUMP_COMMIT_TASK);

                lazyConfiguration(t, new Runnable() {
                    public void run() {
                        t.setCommandLine(GitUtil.getGitPushArgs(conf, gitStatus.getBranch()));
                        t.setSecretValue(conf.getGitHub().getWriteAuthToken());
                    }
                });
            }
        });

        TaskMaker.task(project, BUMP_VERSION_AND_PUSH_TASK, new Action<Task>() {
            public void execute(Task t) {
                t.setDescription("Increments version number, commits and pushes changes to Git repository");
                t.dependsOn(VersioningPlugin.BUMP_VERSION_FILE_TASK, ADD_BUMP_VERSION_TASK);
                t.dependsOn(VERSION_BUMP_COMMIT_TASK, PUSH_BUMP_VERSION_TASK);
            }
        });
    }
}
