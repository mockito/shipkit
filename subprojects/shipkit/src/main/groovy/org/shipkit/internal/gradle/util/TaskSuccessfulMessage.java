package org.shipkit.internal.gradle.util;

import org.gradle.api.Action;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

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
}
