package org.shipkit.internal.gradle.git.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.exec.DefaultProcessRunner;
import org.shipkit.internal.exec.ProcessRunner;
import org.shipkit.internal.util.ExposedForTesting;

/**
 * This task will checkout a certain revision.
 */
public class GitCheckOutTask extends DefaultTask {

    @Input
    private String rev;
    @Input
    private boolean newBranch;
    @Input @Optional
    private File directory;

    private ProcessRunner processRunner;

    /**
     * See {@link #getRev()}
     */
    public void setRev(String rev) {
        this.rev = rev;
    }

    /**
     * Revision to check out
     */
    public String getRev() {
        return rev;
    }

    /**
     * See {@link #isNewBranch()}
     */
    public void setNewBranch(boolean newBranch) {
        this.newBranch = newBranch;
    }

    /**
     * Should checkout create a new branch
     */
    public boolean isNewBranch() {
        return newBranch;
    }

    /**
     * A directory where execute git command
     */
    public File getDirectory() {
        return directory;
    }

    /**
     * See {@link #getDirectory()}
     */
    public void setDirectory(File directory) {
        this.directory = directory;
    }

    @TaskAction
    public void checkOut() {
        getProcessRunner().run(getCommandLine());
    }

    private List<String> getCommandLine() {
        List<String> commandLine = new ArrayList<>();
        commandLine.add("git");
        commandLine.add("checkout");
        if (newBranch) {
            commandLine.add("-b");
        }
        commandLine.add(rev);
        return commandLine;
    }

    private ProcessRunner getProcessRunner() {
        if (processRunner == null) {
            if (directory == null) {
                return new DefaultProcessRunner(getProject().getProjectDir());
            } else {
                return new DefaultProcessRunner(directory);
            }
        }
        return processRunner;
    }

    @ExposedForTesting
    protected void setProcessRunner(ProcessRunner processRunner) {
        this.processRunner = processRunner;
    }
}
