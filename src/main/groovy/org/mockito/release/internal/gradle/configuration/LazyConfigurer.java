package org.mockito.release.internal.gradle.configuration;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.execution.TaskExecutionGraph;
import org.gradle.api.execution.TaskExecutionGraphListener;
import org.mockito.release.internal.util.MultiMap;

/**
 * TODO make it stop implementing TaskExecutionGraphListener
 *
 * Offers means to lazily validate user-specified settings that required for certain tasks
 * (like upload tasks require secret Bintray keys).
 * We don't want to validate the presence of those key is every regular build
 * because it would upset the contributor/developer workflow.
 *
 * We want to validate the settings before any task is executed so that no tasks run if all settings are not supplied.
 */
public class LazyConfigurer implements TaskExecutionGraphListener {

    private MultiMap<Task, Runnable> actions = new MultiMap<Task, Runnable>();

    public void graphPopulated(TaskExecutionGraph graph) {
        for (Task key : actions.keySet()) {
            if (graph.hasTask(key)) {
                for (Runnable r : actions.get(key)) {
                    r.run();
                }
            }
        }
    }

    /**
     * Forces lazy configuration to be triggered for given task
     */
    public static void forceConfiguration(Task task) {
        for (Runnable r : getConfigurer(task.getProject()).actions.get(task)) {
            r.run();
        }
    }

    /**
     * Performs configuration only if given task is in the task execution graph
     */
    public static void lazyConfiguration(Task task, Runnable configuration) {
        LazyConfigurer configurer = getConfigurer(task.getProject());
        configurer.actions.put(task, configuration);
    }

    static LazyConfigurer getConfigurer(Project project) {
        Project rootProject = project.getRootProject();
        //single configurer for the entire build, hooked up to the root project, for simplicity and speed
        //we don't want too many listeners that introduce blocking callbacks to Gradle internals

        LazyConfigurer validator = rootProject.getExtensions().findByType(LazyConfigurer.class);
        if (validator == null) {
            validator = new LazyConfigurer();
            rootProject.getExtensions().add(LazyConfigurer.class.getName(), validator);
            rootProject.getGradle().addListener(validator);
        }
        return validator;
    }
}