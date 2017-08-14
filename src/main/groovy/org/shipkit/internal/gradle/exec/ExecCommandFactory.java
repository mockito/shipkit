package org.shipkit.internal.gradle.exec;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.StopExecutionException;
import org.gradle.process.ExecResult;
import org.gradle.process.ExecSpec;
import org.shipkit.gradle.exec.ExecCommand;

import java.io.File;
import java.util.Collection;

import static java.util.Arrays.asList;

public class ExecCommandFactory {

    private final static Logger LOG = Logging.getLogger(ExecCommandFactory.class);

    public static Action<ExecSpec> ignoreResult() {
        return new Action<ExecSpec>() {
            public void execute(ExecSpec spec) {
                spec.setIgnoreExitValue(true);
            }
        };
    }

    public static Action<ExecSpec> ignoreResult(final File workingDir) {
        return new Action<ExecSpec>() {
            public void execute(ExecSpec spec) {
                spec.setIgnoreExitValue(true);
                spec.setWorkingDir(workingDir);
            }
        };
    }

    public static Action<ExecResult> stopExecution() {
        return new Action<ExecResult>() {
            public void execute(ExecResult exec) {
                if (exec.getExitValue() != 0) {
                    LOG.info("External process returned exit code: {}. Stopping the execution of the task.");
                    //Cleanly stop executing the task, without making the task failed.
                    throw new StopExecutionException();
                }
            }
        };
    }

    private static Action<ExecResult> ensureSucceeded(final String prefix) {
        return new Action<ExecResult>() {
            public void execute(ExecResult result) {
                ensureSucceeded(result, prefix);
            }
        };
    }

    static void ensureSucceeded(ExecResult result, String prefix) {
        if (result.getExitValue() != 0) {
            throw new GradleException("External command failed with exit code " + result.getExitValue() + "\n" +
                    "Please inspect the command output prefixed with '" + prefix.trim() + "' the build log.");
        }
    }

    /**
     * Exec command that will throw the exception when the command line fails.
     * This is the most typical kind of exec command.
     */
    public static ExecCommand execCommand(String description, Collection<String> commandLine) {
        String prefix = defaultPrefix(commandLine);
        return new ExecCommand(prefix, description, commandLine, ignoreResult(), ensureSucceeded(prefix));
    }

    /**
     * See {@link #execCommand(String, Collection)}
     */
    public static ExecCommand execCommand(String description, String ... commandLine) {
        return execCommand(description, asList(commandLine));
    }

    /**
     * See {@link #execCommand(String, Collection)}
     */
    public static ExecCommand execCommand(String description, File workingDir, String ... commandLine) {
        Collection<String> cmd = asList(commandLine);
        String prefix = defaultPrefix(cmd);
        return new ExecCommand(prefix, description, cmd, ignoreResult(workingDir), ensureSucceeded(prefix));
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
        return new ExecCommand(defaultPrefix(commandLine), description, commandLine, ignoreResult(), resultAction);
    }
}
