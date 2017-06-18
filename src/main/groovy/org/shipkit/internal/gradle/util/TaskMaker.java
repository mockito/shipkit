package org.shipkit.internal.gradle.util;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Exec;

import static org.shipkit.internal.gradle.util.StringUtil.join;

/**
 * Handy class that should be used to create tasks.
 * It ensures the correct defaults are used by tasks.
 * It also adds consistent logging for some kinds of tasks (like exec tasks).
 */
public class TaskMaker {

    private static final Logger LOG = Logging.getLogger(TaskMaker.class);

    //TODO remove this property, all current client of this property should be using factory methods
    // available on this class to create tasks
    public final static String TASK_GROUP = "Shipkit";

    /**
     * Creates exec task with preconfigured defaults
     */
    public static Exec execTask(Project project, String name, Action<Exec> configure) {
        final Exec exec = project.getTasks().create(name, Exec.class);
        exec.doFirst(new Action<Task>() {
            public void execute(Task task) {
                LOG.lifecycle("  Running:\n    {}", join(exec.getCommandLine(), " "));
            }
        });
        return configure(configure, exec);
    }

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
        task.setGroup(TASK_GROUP);
        configure.execute(task);
        if(task.getDescription() == null) {
            //TODO unit testable
            throw new IllegalArgumentException("Please provide description for the task!");
        }
        return task;
    }
}
