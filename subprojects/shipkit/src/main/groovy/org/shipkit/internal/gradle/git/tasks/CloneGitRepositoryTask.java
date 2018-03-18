package org.shipkit.internal.gradle.git.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.exec.Exec;
import org.shipkit.internal.exec.ProcessRunner;
import org.shipkit.internal.util.ExposedForTesting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;

/**
 * This task clones git project from {@link #getRepositoryUrl()} to {@link #getTargetDir()}.
 * It supports clone from remote server and from local filesystem.
 * The task execution is skipped if {@link #getTargetDir()} exists and is not empty.
 * If you want to always execute it, use clean task before calling this one.
 * Note that it's a heavy operation and therefore multiple executions in the same build should be avoided.
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
        if (!isTargetEmpty()) {
            LOG.lifecycle("{} - target directory already exists and is not empty. Skipping execution of the task. Exists: {}", getPath(), targetDir);
            return;
        }

        LOG.lifecycle("  Cloning repository {}\n    into {}", repositoryUrl, targetDir);

        getProject().getBuildDir().mkdirs();    // build dir can be not created yet
        ProcessRunner processRunner = Exec.getProcessRunner(getProject().getBuildDir());
        processRunner.run(getCloneCommand());
    }

    //TODO: WW investigate if this method can be removed from public API
    @ExposedForTesting
    List<String> getCloneCommand() {
        List<String> result = new ArrayList<>();
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

    private boolean isTargetEmpty() {
        return !targetDir.exists() || targetDir.list().length == 0;
    }
}
