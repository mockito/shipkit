package org.mockito.release.internal.gradle.util;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Exec;

import static org.mockito.release.internal.gradle.util.StringUtil.join;

/**
 * Handy class that should be used to create tasks.
 * It ensures the correct defaults are used by tasks.
 * It also adds consistent logging for some kinds of tasks (like exec tasks).
 */
public class TaskMaker {

    private static final Logger LOG = Logging.getLogger(TaskMaker.class);

    //TODO remove this property, all current client of this property should be using factory methods
    // available on this class to create tasks
    public final static String TASK_GROUP = "Mockito Release Tools";

    /**
     * Creates exec task with preconfigured defaults
     */
    public static Exec execTask(Project project, String name, Action<Exec> configureTask) {
        return execTask(project, name, false, configureTask);
    }

    /**
     * Creates exec task with preconfigured defaults
     */
    public static Exec execTask(Project project, String name, final boolean quiet, Action<Exec> configureTask) {
        //TODO unit testable
        final Exec exec = project.getTasks().create(name, Exec.class);
        exec.doFirst(new Action<Task>() {
            public void execute(Task task) {
                if (!quiet) {
                    LOG.lifecycle("  Running:\n    {}", join(exec.getCommandLine(), " "));
                }
            }
        });
        return configure(configureTask, exec);
    }

    /**
     * Creates task with preconfigured defaults
     */
    public static Task task(Project project, String name, Action<Task> configureTask) {
        Task task = project.getTasks().create(name);
        return configure(configureTask, task);
    }

    private static <T extends Task> T configure(Action<T> configureTask, T task) {
        task.setGroup(TASK_GROUP);
        configureTask.execute(task);
        if(task.getDescription() == null) {
            //TODO unit testable
            throw new IllegalArgumentException("Please provide description for the task!");
        }
        return task;
    }
}
