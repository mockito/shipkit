package org.shipkit.gradle.exec;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.gradle.exec.ShipkitExec;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Generic Gradle task that allows composing multiple executable command line invocations.
 * Very useful for scenarios where we cannot easily use multiple Gradle Exec tasks.
 */
public class ShipkitExecTask extends DefaultTask {

    private Collection<ExecCommand> execCommands = new LinkedList<ExecCommand>();

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
     * Executes all commands
     */
    @TaskAction public void execCommands() {
        new ShipkitExec().execCommands(this.getExecCommands(), this.getProject());
    }
}
