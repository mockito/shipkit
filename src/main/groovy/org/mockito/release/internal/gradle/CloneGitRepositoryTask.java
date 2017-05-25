package org.mockito.release.internal.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.mockito.release.exec.ProcessRunner;

import java.io.File;

/**
 * This task clone git project from repository to target dir.
 * It support clone from remote server and from local filesystem.
 *
 * TODO ms - when you are ready, please move the new task types to the public packages,
 *   for example "org.mockito.release.*". With 1.0 we need all task types to be public.
 *   It's because users interface with task types when they work with Gradle build scripts.
 *   So it makes sense to be explicit that those types are public and we guarantee compatibility.
 *   See also README.md on the compatibility where I attempted to describe this ;)
 */
public class CloneGitRepositoryTask extends DefaultTask {

    private static final Logger LOG = Logging.getLogger(CloneGitRepositoryTask.class);

    private String repository;
    private File targetDir;

    @TaskAction
    public void cloneRepository() {
        if (repository == null || repository.isEmpty()) {
            throw new RuntimeException("Invalid repository '" + repository + "' given!");
        }

        LOG.lifecycle("  Cloning repository {}\n    into {}", repository, targetDir);

        if (targetDir.exists()) {
            getProject().delete(targetDir);
        }

        getProject().getBuildDir().mkdirs();    // build dir can be not created yet
        ProcessRunner processRunner = org.mockito.release.exec.Exec.getProcessRunner(getProject().getBuildDir());
        processRunner.run("git", "clone", repository, targetDir.getAbsolutePath());
    }

    //TODO ms - let's put javadoc on all public methods of the task
    // No need to put it on "cloneRepository" method because it is not intended to be used by end users.
    // It's nice if javadoc for 'repository' demonstrates an example value
    // When reading the API by looking at method signature
    //   I don't know if repository should be a name of repo or valid url to the repo
    @Input
    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getRepository() {
        return repository;
    }

    @OutputDirectory
    public void setTargetDir(File targetDir) {
        this.targetDir = targetDir;
    }

    public File getTargetDir() {
        return targetDir;
    }
}
