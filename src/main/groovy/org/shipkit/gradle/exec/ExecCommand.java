package org.shipkit.gradle.exec;

import org.gradle.api.Action;
import org.gradle.process.ExecResult;
import org.gradle.process.ExecSpec;

import java.util.Collection;

/**
 * Object that contains information about the executable command line.
 * It has: the command line arguments, action that configures the execution,
 * action that is triggered when the execution is complete.
 */
public class ExecCommand {

    private final static Action NO_OP_ACTION = new Action() {
        public void execute(Object o) {}
    };

    private final static Action ENSURE_SUCCEEDED_ACTION = new Action<ExecResult>() {
        public void execute(ExecResult result) {
            //TODO offer cleaner exception than the one by default offered by Gradle
            result.assertNormalExitValue();
        }
    };

    private final String description;
    private final Collection<String> commandLine;
    private final Action<ExecSpec> setupAction;
    private final Action<ExecResult> resultAction;

    /**
     * Generic command line to be executed
     *
     * @param description human readable description of the command
     * @param commandLine command line to be executed
     * @param setupAction action that configures the command line execution
     * @param resultAction action that is triggered after the command line was executed
     */
    public ExecCommand(String description, Collection<String> commandLine, Action<ExecSpec> setupAction, Action<ExecResult> resultAction) {
        this.description = description;
        this.commandLine = commandLine;
        this.setupAction = setupAction;
        this.resultAction = resultAction;
    }

    /**
     * Exec command that will throw the exception when the command line fails.
     * This is the most typical kind of exec command.
     */
    public ExecCommand(String description, Collection<String> commandLine) {
        this(description, commandLine, NO_OP_ACTION, ENSURE_SUCCEEDED_ACTION);
    }

    /**
     * Exec command with custom result action.
     * Useful if the user needs custom behavior when command line finishes executing.
     * For example, we can ignore the failure.
     */
    public ExecCommand(String description, Collection<String> commandLine, Action<ExecResult> resultAction) {
        this(description, commandLine, NO_OP_ACTION, resultAction);
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

    public String getDescription() {
        return description;
    }
}
