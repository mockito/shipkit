package org.shipkit.internal.gradle.exec;

import org.gradle.api.Action;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.process.ExecResult;
import org.gradle.process.ExecSpec;
import org.shipkit.gradle.exec.ExecCommand;
import org.shipkit.gradle.exec.ShipkitExecTask;
import org.shipkit.internal.exec.ExternalProcessStream;
import org.shipkit.internal.gradle.util.StringUtil;

public class ShipkitExec {

    private final static Logger LOG = Logging.getLogger(ShipkitExec.class);

    public void execCommands(ShipkitExecTask task) {
        for (final ExecCommand execCommand : task.getExecCommands()) {
            ExecResult result = task.getProject().exec(new Action<ExecSpec>() {
                @Override
                public void execute(ExecSpec spec) {
                    spec.setIgnoreExitValue(true);
                    spec.commandLine(execCommand.getCommandLine());
                    spec.setStandardOutput(new ExternalProcessStream(execCommand.getLoggingPrefix(), System.out));
                    spec.setErrorOutput(new ExternalProcessStream(execCommand.getLoggingPrefix(), System.err));

                    execCommand.getSetupAction().execute(spec);

                    LOG.lifecycle("  " + execCommand.getDescription() + ":\n    " + StringUtil.join(execCommand.getCommandLine(), " "));
                }
            });
            execCommand.getResultAction().execute(result);
        }
    }
}
