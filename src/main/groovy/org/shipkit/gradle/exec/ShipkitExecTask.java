package org.shipkit.gradle.exec;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecResult;
import org.gradle.process.ExecSpec;
import org.shipkit.internal.exec.ExternalProcessStream;
import org.shipkit.internal.gradle.util.StringUtil;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Generic Gradle task that allows composing multiple executable command line invocations.
 * Very useful for scenarios where we cannot easily use multiple Gradle Exec tasks.
 */
public class ShipkitExecTask extends DefaultTask {

    private Collection<ExecCommand> execCommands = new LinkedList<ExecCommand>();
    private final static Logger LOG = Logging.getLogger(ShipkitExecTask.class);

    /**
     * Sequence of command line executions.
     * Will be executed sequentially in given order.
     */
    public Collection<ExecCommand> getExecCommands() {
        return execCommands;
    }

    /**
     * See {@link #getExecCommands()}
     */
    public void setExecCommands(Collection<ExecCommand> execCommands) {
        this.execCommands = execCommands;
    }

    @TaskAction public void execCommands() {
        for (final ExecCommand execCommand : execCommands) {
            ExecResult result = getProject().exec(new Action<ExecSpec>() {
                @Override
                public void execute(ExecSpec spec) {
                    //TODO we should expose 'description' on exec command and write it to console before forking process
                    //TODO figure out a clean way of adding unit test coverage for it

                    spec.setIgnoreExitValue(true);
                    spec.commandLine(execCommand.getCommandLine());
                    //TODO move prefix onto the ExecCommand with default
                    String firstArg = execCommand.getCommandLine().iterator().next();
                    spec.setStandardOutput(new ExternalProcessStream(firstArg, System.out));
                    spec.setErrorOutput(new ExternalProcessStream(firstArg, System.err));

                    execCommand.getSetupAction().execute(spec);

                    LOG.lifecycle("  Executing:\n    " + StringUtil.join(execCommand.getCommandLine(), " "));
                }
            });
            execCommand.getResultAction().execute(result);
        }
    }
}
