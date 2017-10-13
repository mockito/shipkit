package org.shipkit.internal.gradle.util;

import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.internal.notes.util.Supplier;

public class TaskSuccessfulMessage {

    private final static Logger LOG = Logging.getLogger(TaskSuccessfulMessage.class);

    /**
     * Writes message to the console when task completes successfully.
     * Basically adds 'doLast' action with log message.
     */
    public static void logOnSuccess(Task task, final String message) {
        task.doLast(new Action<Task>() {
            @Override
            public void execute(Task task) {
                LOG.lifecycle(message);
            }
        });
    }

    public static void logOnSuccess(Task task, final Supplier<String> supplier) {
        task.doLast(new Action<Task>() {
            @Override
            public void execute(Task task) {
                LOG.lifecycle(supplier.get());
            }
        });
    }
}
