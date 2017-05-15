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
 */
public class CloneGitRepositoryTask extends DefaultTask {

    private static final Logger LOG = Logging.getLogger(CloneGitRepositoryTask.class);

    private String repository;
    private File targetDir;

    @TaskAction
    public void cloneRepository() {
        LOG.lifecycle("  Clone repository");
        getProject().getBuildDir().mkdirs();    // build dir can be not created yet
        ProcessRunner processRunner = org.mockito.release.exec.Exec.getProcessRunner(getProject().getBuildDir());
        processRunner.run("git", "clone", repository, targetDir.getAbsolutePath());
    }

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
