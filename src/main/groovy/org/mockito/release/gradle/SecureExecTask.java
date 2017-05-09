package org.mockito.release.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.mockito.release.exec.DefaultProcessRunner;

import java.util.LinkedList;
import java.util.List;

/**
 * Similar to Gradle's built-in {@link org.gradle.api.tasks.Exec}
 * but it does not expose the command line parameters when build is executed with '-i' (--info) level.
 * It masks secret value configured via {@link #setSecretValue(String)} from logging, task output and exception messages.
 * Replaces secret value with "[SECRET]".
 * It really helps debugging if we can see the output and logging without exposing secret values like GitHub auth token.
 */
public class SecureExecTask extends DefaultTask {

    private List<String> commandLine = new LinkedList<String>();
    private String secretValue;
    private boolean dryRun;

    /**
     * @return command line to be executed
     */
    public List<String> getCommandLine() {
        return commandLine;
    }

    /**
     * @param commandLine command line to be executed
     */
    public void setCommandLine(List<String> commandLine) {
        this.commandLine = commandLine;
    }

    /**
     * @return value to be secured, e.g. masked from forked process output and logging
     */
    public String getSecretValue() {
        return secretValue;
    }

    /**
     * See {@link #getSecretValue()}
     */
    public void setSecretValue(String secretValue) {
        this.secretValue = secretValue;
    }

    public boolean isDryRun() {
        return dryRun;
    }

    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    @TaskAction public void secureExec() {
        new DefaultProcessRunner(getProject().getProjectDir())
            .setSecretValue(secretValue)
            .setDryRun(dryRun)
            .run(commandLine);
    }
}
