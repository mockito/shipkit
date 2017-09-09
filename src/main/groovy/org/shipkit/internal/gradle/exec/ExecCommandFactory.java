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
import java.util.List;

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
            throw new GradleException("External process failed with exit code " + result.getExitValue() + "\n" +
                    "Please inspect the command output prefixed with '" + prefix.trim() + "' the build log.");
        }
    }

    /**
     * Exec command that will throw the exception when the command line fails.
     * This is the most typical kind of exec command.
     */
    public static ExecCommand execCommand(String description, List<String> commandLine) {
        String prefix = defaultPrefix(commandLine);
        return new ExecCommand(prefix, description, commandLine, ignoreResult(), ensureSucceeded(prefix));
    }

    /**
     * See {@link #execCommand(String, List)}
     */
    public static ExecCommand execCommand(String description, String ... commandLine) {
        return execCommand(description, asList(commandLine));
    }

    /**
     * See {@link #execCommand(String, List)}
     */
    public static ExecCommand execCommand(String description, File workingDir, String ... commandLine) {
        List<String> cmd = asList(commandLine);
        String prefix = defaultPrefix(cmd);
        return new ExecCommand(prefix, description, cmd, ignoreResult(workingDir), ensureSucceeded(prefix));
    }

    private static String defaultPrefix(List<String> commandLine) {
        return "[" + commandLine.get(0) + "] ";
    }

    /**
     * Exec command with custom result action.
     * Useful if the user needs custom behavior when command line finishes executing.
     * For example, we can ignore the failure.
     */
    public static ExecCommand execCommand(String description, List<String> commandLine, Action<ExecResult> resultAction) {
        return new ExecCommand(defaultPrefix(commandLine), description, commandLine, ignoreResult(), resultAction);
    }
}
