package org.shipkit.gradle.git;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.gradle.git.tasks.IdentifyGitBranch;

import java.io.File;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Gets information about current git branch and keeps the value in the task as reference.
 */
public class IdentifyGitBranchTask extends DefaultTask {

    private List<String> commandLine = asList("git", "rev-parse", "--abbrev-ref", "HEAD");
    private File workDir = getProject().getRootDir();
    private String branch;

    private final IdentifyGitBranch identifyBranch = new IdentifyGitBranch();

    @TaskAction public void identifyBranch() {
        identifyBranch.identifyBranch(this, branch);
    }

    /**
     * The current Git branch.
     * Throws an exception if the branch was not yet identified.
     * The branch is identified either by running the task or when {@link #setBranch(String)} is explicitly set on the task.
     */
    public String getBranch() {
        return identifyBranch.getBranch(branch);
    }

    /**
     * Use it to set the branch explicitly and avoid making the task execute git process to identify branch.
     * See {@link #getBranch()}
     */
    public void setBranch(String branch) {
        this.branch = branch;
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
