package org.shipkit.internal.gradle.git;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.shipkit.internal.gradle.git.tasks.CloneGitRepositoryTask;
import org.shipkit.internal.gradle.util.TaskMaker;

import java.io.File;

import static org.shipkit.internal.util.RepositoryNameUtil.repositoryNameToCamelCase;
import static org.shipkit.internal.util.RepositoryNameUtil.repositoryNameToCapitalizedCamelCase;

public class CloneGitRepositoryTaskFactory {

    /**
     * Creates an instance of CloneGitRepositoryTask for given {@param #consumerRepository} in the root project
     * or returns already existing one.
     */
    public static CloneGitRepositoryTask createCloneTask(final Project project, final String gitHubUrl, final String consumerRepository) {
        String taskName = "clone" + repositoryNameToCapitalizedCamelCase(consumerRepository);
        Project taskProject = project.getRootProject();

        CloneGitRepositoryTask alreadyExistingTask = (CloneGitRepositoryTask) taskProject.getTasks().findByName(taskName);
        if (alreadyExistingTask != null) {
            return alreadyExistingTask;
        }

        return TaskMaker.task(taskProject,
            taskName,
            CloneGitRepositoryTask.class,
            new Action<CloneGitRepositoryTask>() {
                @Override
                public void execute(final CloneGitRepositoryTask task) {
                    task.setDescription("Clones consumer repo " + consumerRepository + " into a temporary directory.");
                    task.setRepositoryUrl(gitHubUrl + "/" + consumerRepository);
                    task.setTargetDir(getConsumerRepoCloneDir(project, consumerRepository));
                }
            });
    }

    /**
     * Returns temporary dir where clone of {@param #consumerRepository} is stored.
     */
    public static File getConsumerRepoCloneDir(Project project, String consumerRepository) {
        return new File(project.getRootProject().getBuildDir().getAbsolutePath() + "/downstream/" + repositoryNameToCamelCase(consumerRepository));
    }
}
