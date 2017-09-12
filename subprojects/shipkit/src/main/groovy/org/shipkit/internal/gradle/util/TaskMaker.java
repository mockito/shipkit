package org.shipkit.internal.gradle.util;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;

/**
 * Handy class that should be used to create tasks.
 * It ensures the correct defaults are used by tasks.
 * It also adds consistent logging for some kinds of tasks (like exec tasks).
 */
public class TaskMaker {

    /**
     * Creates task with preconfigured defaults
     */
    public static Task task(Project project, String name, Action<Task> configure) {
        Task task = project.getTasks().create(name);
        return configure(configure, task);
    }

    /**
     * Creates task of specific type with preconfigured defaults
     */
    public static <T extends Task> T task(Project project, String name, Class<T> taskType, Action<T> configure) {
        T task = project.getTasks().create(name, taskType);
        return configure(configure, task);
    }

    private static <T extends Task> T configure(Action<T> configure, T task) {
        task.setGroup("Shipkit");
        configure.execute(task);
        if (task.getDescription() == null) {
            //TODO unit testable
            throw new IllegalArgumentException("Please provide description for the task!");
        }
        return task;
    }
}
