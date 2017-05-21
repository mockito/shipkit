package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Exec;
import org.mockito.release.gradle.ReleaseConfiguration;
import org.mockito.release.internal.gradle.util.TaskMaker;

import static org.mockito.release.internal.gradle.util.GitUtil.getTag;

/**
 * Adds Git-specific tasks needed for the release process.
 */
public class GitPlugin implements Plugin<Project> {

    private static final Logger LOG = Logging.getLogger(GitPlugin.class);

    static final String COMMIT_CLEANUP_TASK = "gitCommitCleanUp";
    static final String TAG_CLEANUP_TASK = "gitTagCleanUp";

    public void apply(final Project project) {
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();

        TaskMaker.execTask(project, COMMIT_CLEANUP_TASK, new Action<Exec>() {
            public void execute(final Exec t) {
                t.setDescription("Removes last commit, using 'reset --hard HEAD~'");
                //TODO replace with combination of 'git reset --soft HEAD~ && git stash' so that we don't lose commits
                t.commandLine("git", "reset", "--hard", "HEAD~");
            }
        });

        TaskMaker.execTask(project, TAG_CLEANUP_TASK, new Action<Exec>() {
            public void execute(final Exec t) {
                t.setDescription("Deletes version tag '" + getTag(conf, project) + "'");
                t.commandLine("git", "tag", "-d", getTag(conf, project));
            }
        });
    }
}
