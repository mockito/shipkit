package org.shipkit.gradle.exec;

import org.gradle.api.Action;
import org.gradle.process.ExecResult;
import org.gradle.process.ExecSpec;

import java.util.Collection;

/**
 * Object that contains information about the executable command line.
 * It has: the command line arguments, action that configures the execution,
 * action that is triggered when the execution is complete.
 * <p>
 * To minimize public API and improve maintenance, this class should remain a value object, without any behavior.
 */
public class ExecCommand {

    private final String description;
    private final Collection<String> commandLine;
    private final Action<ExecSpec> setupAction;
    private final Action<ExecResult> resultAction;
    private final String loggingPrefix;

    /**
     * Generic command line to be executed
     *
     * @param loggingPrefix see {@link #getLoggingPrefix()}
     * @param description human readable description of the command
     * @param commandLine command line to be executed
     * @param setupAction action that configures the command line execution
     * @param resultAction action that is triggered after the command line was executed
     */
    public ExecCommand(String loggingPrefix, String description, Collection<String> commandLine, Action<ExecSpec> setupAction, Action<ExecResult> resultAction) {
        this.loggingPrefix = loggingPrefix;
        this.description = description;
        this.commandLine = commandLine;
        this.setupAction = setupAction;
        this.resultAction = resultAction;
    }

    /**
     * Command line to be executed.
     */
    public Collection<String> getCommandLine() {
        return commandLine;
    }

    /**
     * The action that configures the execution of command line
     */
    public Action<ExecSpec> getSetupAction() {
        return setupAction;
    }

    /**
     * The action that takes place after the command line is executed
     */
    public Action<ExecResult> getResultAction() {
        return resultAction;
    }

    /**
     * Description of the command, for example: "Checking out git revision"
     */
    public String getDescription() {
        return description;
    }

    /**
     * Logging prefix to be used to prefix every line of output captured from running the command.
     */
    public String getLoggingPrefix() {
        return loggingPrefix;
    }
}
