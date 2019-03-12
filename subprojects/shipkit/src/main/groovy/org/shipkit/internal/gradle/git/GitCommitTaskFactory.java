package org.shipkit.internal.gradle.git;

import org.gradle.api.Project;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.gradle.git.GitCommitTask;
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin;
import org.shipkit.internal.gradle.util.TaskMaker;

public class GitCommitTaskFactory {

    public static GitCommitTask createGitCommitTask(Project project, String taskName, String description) {
        ShipkitConfiguration conf = project.getPlugins().apply(ShipkitConfigurationPlugin.class).getConfiguration();

        return TaskMaker.task(project, taskName, GitCommitTask.class, task -> {
            task.setDescription(description);
            task.setGitUserName(conf.getGit().getUser());
            task.setGitUserEmail(conf.getGit().getEmail());
            task.setCommitMessagePostfix(conf.getGit().getCommitMessagePostfix());
        });
    }
}
