package org.mockito.release.internal.gradle.util;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.execution.TaskExecutionGraph;
import org.gradle.api.execution.TaskExecutionGraphListener;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Offers means to lazily configure Gradle tasks, only when the tasks are in the task graph.
 * This way, we identify missing settings before tasks are executed.
 * This is important, because when tasks are executed they can do stuff that is hard to reverse.
 * So we want to do all the validation beforehand.
 */
public class SettingsConfigurer implements TaskExecutionGraphListener {

    private Map<Task, Runnable> actions = new LinkedHashMap<Task, Runnable>();

    public void graphPopulated(TaskExecutionGraph graph) {
        for (Map.Entry<Task, Runnable> e : actions.entrySet()) {
            if (graph.hasTask(e.getKey())) {
                e.getValue().run();
            }
        }
    }

    /**
     * Gets the configurer for the project. Configurer is a singleton hooked up to the root project.
     */
    public static SettingsConfigurer getConfigurer(Project project) {
        Project rootProject = project.getRootProject();
        //single configurer for the entire build, hooked up to the root project, for simplicity and speed
        //we don't want too many listeners that introduce blocking callbacks to Gradle internals

        SettingsConfigurer validator = rootProject.getExtensions().findByType(SettingsConfigurer.class);
        if (validator == null) {
            validator = new SettingsConfigurer();
            rootProject.getExtensions().add(SettingsConfigurer.class.getName(), validator);
            rootProject.getGradle().addListener(validator);
        }
        return validator;
    }

    /**
     * Lazily configures given task, only when the task is included in the task graph
     */
    public void configureLazily(Task task, Runnable action) {
        actions.put(task, action);
    }
}