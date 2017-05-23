package org.mockito.release.internal.gradle.util;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.mockito.release.internal.gradle.GitCommitTask;
import org.mockito.release.internal.gradle.GitPlugin;

import java.io.File;
import java.util.List;

/**
 * Utility methods for {@link org.mockito.release.internal.gradle.GitPlugin}
 */
public class GitPluginUtil {

    public static void registerChangesIfGitPluginApplied(final Project project, final List<File> changedFiles,
                                                         final String changeDescription, final Task changingTask){
        project.getPlugins().withType(GitPlugin.class, new Action<GitPlugin>() {
            @Override
            public void execute(GitPlugin gitPushPlugin) {
                GitCommitTask gitCommitTask = (GitCommitTask) project.getTasks().findByName(GitPlugin.GIT_COMMIT_TASK);
                gitCommitTask.addChange(changedFiles, changeDescription, changingTask);
            }
        });
    }
}
