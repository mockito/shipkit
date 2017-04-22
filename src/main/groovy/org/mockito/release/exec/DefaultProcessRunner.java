package org.mockito.release.exec;

import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.mockito.release.notes.util.ReleaseNotesException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.release.internal.gradle.util.StringUtil.join;

//TODO since this class is already public, probably it's best if we keep things simple and remove it from the public API
//1. Remove the ProcessRunner interface and Exec class
//2. Move the runner to the internal package
public class DefaultProcessRunner implements ProcessRunner {

    private static final Logger LOG = Logging.getLogger(DefaultProcessRunner.class);
    private final File workDir;
    private String secretValue;

    public DefaultProcessRunner(File workDir) {
        this.workDir = workDir;
    }

    public String run(String... commandLine) {
        return run(asList(commandLine));
    }

    public String run(List<String> commandLine) {
        return run(LOG, commandLine);
    }

    String run(Logger log, List<String> commandLine) {
        // WARNING!!! ensure that masked command line is used for all logging!!!
        String maskedCommandLine = mask(join(commandLine, " "));
        log.lifecycle("  Executing:\n    " + maskedCommandLine);

        ProcessResult result = executeProcess(commandLine, maskedCommandLine);

        if (result.getExitValue() != 0) {
            throw new GradleException("Execution of command failed (exit code " + result.getExitValue() + "):\n" +
                    "  " + maskedCommandLine + "\n" +
                    "Captured command output:\n" + result.getOutput());
        } else {
            return result.getOutput();
        }
    }

    private ProcessResult executeProcess(List<String> commandLine, String maskedCommandLine) {
        ProcessResult result;
        try {
            Process process = new ProcessBuilder(commandLine).directory(workDir).redirectErrorStream(true).start();
            String output = mask(readFully(new BufferedReader(new InputStreamReader(process.getInputStream()))));
            result = new ProcessResult(output, process);
        } catch (Exception e) {
            throw new ReleaseNotesException("Problems executing command:\n  " + maskedCommandLine, e);
        }
        return result;
    }

    private String mask(String text) {
        if (secretValue == null) {
            return text;
        }
        return text.replace(secretValue, "[SECRET]");
    }

    private static String readFully(BufferedReader reader) throws IOException {
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } finally {
            reader.close();
        }
    }

    /**
     * @param secretValue to be masked from the output and logging
     * @return this runner
     */
    public DefaultProcessRunner setSecretValue(String secretValue) {
        this.secretValue = secretValue;
        return this;
    }

    private static class ProcessResult {
        private final String output;
        private final Process process;

        public ProcessResult(String output, Process process) {
            this.output = output;
            this.process = process;
        }

        public String getOutput() {
            return output;
        }

        public int getExitValue() {
            return process.exitValue();
        }
    }
}
