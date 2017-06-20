package org.shipkit.gradle.git;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.exec.Exec;

import java.io.File;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Gets information about current git branch and keeps the value in the task as reference.
 *
 * TODO: decom git status plugin, move git related tasks and internal classes to git package
 */
public class IdentifyGitBranchTask extends DefaultTask {

    private List<String> commandLine = asList("git", "rev-parse", "--abbrev-ref", "HEAD");
    private File workDir = getProject().getRootDir();
    private String branch;

    @TaskAction public void identifyBranch() {
        this.branch = Exec.getProcessRunner(workDir)
                .run(commandLine)
                .trim();
    }

    /**
     * The current Git branch.
     * Throws an exception if the task was not executed yet.
     */
    public String getBranch() {
        if (branch == null) {
            throw new BranchNotAvailableException("Don't know the branch yet because " + getPath() + " task was not executed yet!");
        }
        return branch;
    }

    /**
     * The git command line used to identify the branch
     */
    public List<String> getCommandLine() {
        return commandLine;
    }

    /**
     * See {@link #getCommandLine()}
     */
    public void setCommandLine(List<String> commandLine) {
        this.commandLine = commandLine;
    }

    /**
     * Working dir where git command {@link #getCommandLine()} is executed
     */
    public File getWorkDir() {
        return workDir;
    }

    /**
     * See {@link #getWorkDir()}
     */
    public void setWorkDir(File workDir) {
        this.workDir = workDir;
    }

    /**
     * Thrown when branch information is not available yet.
     */
    public static class BranchNotAvailableException extends GradleException {
        public BranchNotAvailableException(String message) {
            super(message);
        }
    }
}
