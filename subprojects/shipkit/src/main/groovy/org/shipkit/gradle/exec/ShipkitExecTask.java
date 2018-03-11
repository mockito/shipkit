package org.shipkit.gradle.exec;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.gradle.exec.ShipkitExec;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Generic Gradle task that has few advantages over Gradle's stock Exec task.
 * It allows composing multiple executable command line invocations.
 * It also adds nice prefix in all logging so that it's more visible that the output comes from external process.
 */
public class ShipkitExecTask extends DefaultTask {

    private Collection<ExecCommand> execCommands = new LinkedList<>();

    /**
     * Executes all commands
     */
    @TaskAction public void execCommands() {
        new ShipkitExec().execCommands(this.getExecCommands(), this.getProject());
    }

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

    /**
     * Appends single exec command to the task
     */
    public void execCommand(ExecCommand execCommand) {
       execCommands.add(execCommand);
    }
}
