package org.shipkit.internal.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.exec.ProcessRunner;
import org.shipkit.internal.exec.Exec;

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

    @Input
    public void setWorkDir(File workDir) {
        this.workDir = workDir;
    }

    @Input
    public void setCommand(List<String> command) {
        this.command = command;
    }

    @Input
    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    @OutputFile
    public void setBuildOutputFile(File file) {
        buildOutput = file;
    }

}
