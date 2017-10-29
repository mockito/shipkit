package org.shipkit.internal.gradle.git.tasks;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.exec.Exec;
import org.shipkit.internal.exec.ProcessRunner;
import org.shipkit.internal.gradle.util.TaskMaker;
import org.shipkit.internal.util.ExposedForTesting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;
import static org.shipkit.internal.util.RepositoryNameUtil.repositoryNameToCamelCase;
import static org.shipkit.internal.util.RepositoryNameUtil.repositoryNameToCapitalizedCamelCase;

/**
 * This task clone git project from repository to target dir.
 * It support clone from remote server and from local filesystem.
 *
 * TODO ms - when you are ready, please move the new task types to the public packages,
 *   for example "org.shipkit.gradle.*". With 1.0 we need all task types to be public.
 *   It's because users interface with task types when they work with Gradle build scripts.
 *   So it makes sense to be explicit that those types are public and we guarantee compatibility.
 *   See also README.md on the compatibility where I attempted to describe this ;)
 */
public class CloneGitRepositoryTask extends DefaultTask {

    private static final Logger LOG = Logging.getLogger(CloneGitRepositoryTask.class);

    private String repositoryUrl;
    private File targetDir;
    private int depth;

    @TaskAction
    public void cloneRepository() {
        LOG.lifecycle("  Cloning repository {}\n    into {}", repositoryUrl, targetDir);
        getProject().getBuildDir().mkdirs();    // build dir can be not created yet
        ProcessRunner processRunner = Exec.getProcessRunner(getProject().getBuildDir());
        processRunner.run(getCloneCommand());
    }

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

    @ExposedForTesting
    List<String> getCloneCommand() {
        List<String> result = new ArrayList<String>();
        result.add("git");
        result.add("clone");
        if (depth != 0) {
            result.add("--depth");
            result.add(valueOf(depth));
        }
        result.add(repositoryUrl);
        result.add(targetDir.getAbsolutePath());
        return result;
    }

    //TODO ms - when we make this task public, let's put javadoc on all public methods of the task
    // No need to put it on "cloneRepository" method because it is not intended to be used by end users.
    // It's nice if javadoc for 'repository' demonstrates an example value
    // When reading the API by looking at method signature

    //   I don't know if repository should be a name of repo or valid url to the repo
    // TODO sf we clone from *-pristine to *-work, so we need url here - I renamed field for better readability

    /**
     * See {@link #getRepositoryUrl()}
     * @param repositoryUrl
     */
    @Input
    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    /**
     * Repository URL, clone source location. It accept any kind of url accepted by git clone command.
     * Examples:
     * <ul>
     *      <li>https://github.com/mockito/mockito</li>
     *      <li>/Users/mstachniuk/code/mockito</li>
     *      <li>file:///Users/mstachniuk/code/mockito</li>
     * </ul>
     */
    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    /**
     * See {@link #getTargetDir()}
     */
    @OutputDirectory
    public void setTargetDir(File targetDir) {
        this.targetDir = targetDir;
    }

    /**
     * A path where to clone a repository.
     */
    public File getTargetDir() {
        return targetDir;
    }

    /**
     * Truncate a history to the specified number of commits. In other words it makes a shallow clone.
     * This input is optional, default set to 0 (zero) what means a full clone
     */
    public int getDepth() {
        return depth;
    }

    /**
     * See {@link #getDepth()}
     * @param depth
     */
    @Optional
    @Input
    public void setDepth(int depth) {
        this.depth = depth;
    }
}
