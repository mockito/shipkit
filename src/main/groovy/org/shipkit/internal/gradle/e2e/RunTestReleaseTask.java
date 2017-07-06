package org.shipkit.internal.gradle.e2e;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.exec.Exec;
import org.shipkit.internal.exec.ProcessRunner;

import java.io.File;
import java.util.List;

/**
 * This task run external process and additionally store output of external process to file.
 */
public class RunTestReleaseTask extends DefaultTask {

    private static final Logger LOG = Logging.getLogger(RunTestReleaseTask.class);

    private List<String> command;
    private File buildOutput;
    private File workDir;
    private String repoName;

    @TaskAction
    public void runTest() {
        LOG.lifecycle("  Run test of {}. The output will be save in {}", repoName, buildOutput.getAbsoluteFile());
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
     * Repository (or project) name, for example: mockito
     */
    public String getRepoName() {
        return repoName;
    }

    /**
     * See {@link #getRepoName()}
     */
    @Input
    public void setRepoName(String repoName) {
        this.repoName = repoName;
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
