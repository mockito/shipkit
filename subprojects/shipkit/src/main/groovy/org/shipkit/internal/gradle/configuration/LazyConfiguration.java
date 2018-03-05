package org.shipkit.internal.gradle.configuration;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.execution.TaskExecutionGraph;
import org.gradle.api.execution.TaskExecutionGraphListener;
import org.shipkit.internal.util.ExposedForTesting;
import org.shipkit.internal.util.MultiMap;

/**
 * Offers means to lazily validate user-specified settings that required for certain tasks
 * (like upload tasks require secret Bintray keys).
 * We don't want to validate the presence of those key is every regular build
 * because it would upset the contributor/developer workflow.
 *
 * We want to validate the settings before any task is executed so that no tasks run if all required settings are not supplied.
 *
 * See {@link #lazyConfiguration(Task, Runnable)}.
 * See also {@link DeferredConfiguration}.
 */
public class LazyConfiguration {

    private final MultiMap<Task, Runnable> actions = new MultiMap<>();

    @ExposedForTesting
    final TaskExecutionGraphListener listener = new TaskExecutionGraphListener() {
        public void graphPopulated(TaskExecutionGraph graph) {
            for (Task key : actions.keySet()) {
                if (graph.hasTask(key)) {
                    for (Runnable r : actions.get(key)) {
                        //TODO add 'info' level logging explaining what happens. Similar to how we do it in DeferredConfiguration
                        r.run();
                    }
                }
            }
        }
    };

    /**
     * Forces lazy configuration to be triggered for given task
     */
    @ExposedForTesting
    public static void forceConfiguration(Task task) {
        for (Runnable r : getConfigurer(task.getProject()).actions.get(task)) {
            r.run();
        }
    }

    /**
     * Performs configuration only if given task is in the task execution graph.
     * Very useful if the task needs information such as secret credentials / api keys.
     * We validate for presence of those secrets only when the task is scheduled in the graph.
     * This way, normal builds don't require users to provide secrets.
     * Only release builds will fail early when secrets are not provided.
     * <p>
     * Lazy configuration action should not apply other plugins, add tasks or add new task relationships (like dependsOn).
     * It will not work because Gradle does not allow adding tasks after the task graph has be already created.
     */
    public static void lazyConfiguration(Task task, Runnable configuration) {
        LazyConfiguration configurer = getConfigurer(task.getProject());
        configurer.actions.put(task, configuration);
    }

    /**
     * Gets singleton configurer from the root project
     */
    @ExposedForTesting
    static LazyConfiguration getConfigurer(Project project) {
        Project rootProject = project.getRootProject();
        //single configurer for the entire build, hooked up to the root project, for simplicity and speed
        //we don't want too many listeners that introduce blocking callbacks to Gradle internals

        LazyConfiguration configurer = rootProject.getExtensions().findByType(LazyConfiguration.class);
        if (configurer == null) {
            configurer = new LazyConfiguration();
            rootProject.getExtensions().add(LazyConfiguration.class.getName(), configurer);
            rootProject.getGradle().addListener(configurer.listener);
        }
        return configurer;
    }
}
