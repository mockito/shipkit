package org.shipkit.internal.gradle.exec;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.process.ExecResult;
import org.gradle.process.ExecSpec;
import org.shipkit.gradle.exec.ExecCommand;
import org.shipkit.internal.exec.ExternalProcessStream;
import org.shipkit.internal.gradle.util.StringUtil;

import java.io.File;
import java.util.Collection;

public class ShipkitExec {

    private final static Logger LOG = Logging.getLogger(ShipkitExec.class);

    /**
     * Execute commands and print execution summary.
     * @param execCommands  Commands to execute
     * @param project       Gradle Project instance
     * @param workingDir    Working directory where command will be executed, it may be null
     */
    public void execCommands(Collection<ExecCommand> execCommands, Project project, File workingDir) {
        for (final ExecCommand execCommand : execCommands) {
            ExecResult result = project.exec(new Action<ExecSpec>() {
                @Override
                public void execute(ExecSpec spec) {
                    spec.setIgnoreExitValue(true);
                    spec.commandLine(execCommand.getCommandLine());
                    spec.setStandardOutput(new ExternalProcessStream(execCommand.getLoggingPrefix(), System.out));
                    spec.setErrorOutput(new ExternalProcessStream(execCommand.getLoggingPrefix(), System.err));
                    if (workingDir != null) {
                        spec.setWorkingDir(workingDir);
                    }

                    execCommand.getSetupAction().execute(spec);

                    LOG.lifecycle("  " + execCommand.getDescription() + ":\n    " + StringUtil.join(execCommand.getCommandLine(), " "));
                }
            });
            LOG.lifecycle("  External process {} completed.", execCommand.getLoggingPrefix().trim());
            execCommand.getResultAction().execute(result);
        }
    }
}
