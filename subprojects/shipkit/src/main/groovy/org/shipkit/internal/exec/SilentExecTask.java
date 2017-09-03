package org.shipkit.internal.exec;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.List;

/**
 * This task run external process and additionally store output of external process to file.
 */
public class SilentExecTask extends DefaultTask {

    private List<String> command;
    private File buildOutput;
    private File workDir;

    @TaskAction
    public void runTest() {
        ProcessRunner processRunner = Exec.getProcessRunner(workDir, buildOutput);
        processRunner.run(command);
    }

    /**
     * A work directory where command will be executed
     */
    public File getWorkDir() {
        return workDir;
    }

    /**
     * See {@link #getWorkDir()}
     */
    @Input
    public void setWorkDir(File workDir) {
        this.workDir = workDir;
    }

    /**
     * Command list to execute, for example: ["./gradlew", "clean", "install"]
     */
    public List<String> getCommand() {
        return command;
    }

    /**
     * See {@link #getCommand()}
     */
    @Input
    public void setCommand(List<String> command) {
        this.command = command;
    }

    /**
     * A File where command output will be stored.
     */
    public File getBuildOutput() {
        return buildOutput;
    }

    /**
     * See: {@link #getBuildOutput()}
     */
    @OutputFile
    public void setBuildOutputFile(File file) {
        buildOutput = file;
    }

}
