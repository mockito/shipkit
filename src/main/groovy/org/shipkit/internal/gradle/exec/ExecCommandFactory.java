package org.shipkit.internal.gradle.exec;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.process.ExecResult;
import org.shipkit.gradle.exec.ExecCommand;

import java.util.Collection;

import static java.util.Arrays.asList;

public class ExecCommandFactory {

    private final static Action NO_OP_ACTION = new Action() {
        public void execute(Object o) {
        }
    };

    private final static Action ENSURE_SUCCEEDED_ACTION = new Action<ExecResult>() {
        public void execute(ExecResult result) {
            ensureSucceeded(result);
        }
    };

    static void ensureSucceeded(ExecResult result) {
        if (result.getExitValue() != 0) {
            throw new GradleException("Command execution failed. The exit code was: " + result.getExitValue() + "\n" +
                    "Please inspect the process output prefixed in the build log.");
        }
    }

    /**
     * Exec command that will throw the exception when the command line fails.
     * This is the most typical kind of exec command.
     */
    public static ExecCommand execCommand(String description, Collection<String> commandLine) {
        return new ExecCommand(defaultPrefix(commandLine), description, commandLine, NO_OP_ACTION, ENSURE_SUCCEEDED_ACTION);
    }

    /**
     * See {@link #execCommand(String, Collection)}
     */
    public static ExecCommand execCommand(String description, String ... commandLine) {
        return execCommand(description, asList(commandLine));
    }

    private static String defaultPrefix(Collection<String> commandLine) {
        //by default, we are using the first argument as prefix
        return "[" + commandLine.iterator().next() + "] ";
    }

    /**
     * Exec command with custom result action.
     * Useful if the user needs custom behavior when command line finishes executing.
     * For example, we can ignore the failure.
     */
    public static ExecCommand execCommand(String description, Collection<String> commandLine, Action<ExecResult> resultAction) {
        return new ExecCommand(defaultPrefix(commandLine), description, commandLine, NO_OP_ACTION, resultAction);
    }
}
