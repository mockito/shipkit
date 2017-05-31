package org.shipkit.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.shipkit.internal.gradle.util.TaskMaker;

/**
 * Creates task initShipkit that all other init tasks should depend on
 * so that running it would create all configuration needed to start Shipkit
 */
public class BootstrapPlugin implements Plugin<Project> {

    public static final String INIT_SHIPKIT_TASK = "initShipkit";

    @Override
    public void apply(final Project project) {

        TaskMaker.task(project, INIT_SHIPKIT_TASK, new Action<Task>() {
            @Override
            public void execute(Task t) {
                t.setDescription("Initializes shipkit");
            }
        });
    }
}
