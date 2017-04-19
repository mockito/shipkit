package org.mockito.release.internal.gradle.util;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.execution.TaskExecutionGraph;
import org.gradle.api.execution.TaskExecutionGraphListener;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * TODO update the docs, explain use cases, explain when to use various methods
 * TODO make it stop implementing TaskExecutionGraphListener
 *
 * Offers means to lazily configure Gradle tasks, only when the tasks are in the task graph.
 * This way, we identify missing settings before tasks are executed.
 * This is important, because when tasks are executed they can do stuff that is hard to reverse.
 * So we want to do all the validation beforehand.
 */
public class LazyValidator implements TaskExecutionGraphListener {

    private Map<Task, Runnable> actions = new LinkedHashMap<Task, Runnable>();
    private Map<Task, LazyValidation> validations = new LinkedHashMap<Task, LazyValidation>();

    public void graphPopulated(TaskExecutionGraph graph) {
        for (Map.Entry<Task, Runnable> e : actions.entrySet()) {
            if (graph.hasTask(e.getKey())) {
                e.getValue().run();
            }
        }

        for (Map.Entry<Task, LazyValidation> v : validations.entrySet()) {
            if (graph.hasTask(v.getKey())) {
                for (RequiredValue required : v.getValue().required) {
                    Object value;
                    try {
                        value = required.getter.call();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    if (value == null) {
                        throw new GradleException(required.validationMessage);
                    }
                }
            }
        }
    }

    /**
     * Performs validation only if given task is in the task execution graph.
     * TODO explain the use case here or in the top level type.
     */
    public static LazyValidation lazyValidation(Task task) {
        LazyValidator configurer = getConfigurer(task.getProject());
        return configurer.addValidation(task);
    }

    private LazyValidation addValidation(Task task) {
        LazyValidation lazyValidation = validations.get(task);
        if (lazyValidation == null) {
            lazyValidation = new LazyValidation();
            validations.put(task, lazyValidation);
        }
        return lazyValidation;
    }

    /**
     * @deprecated please use {@link #lazyValidation(Task)} or {@link DeferredConfiguration#deferredConfiguration(Project, Action)}.
     */
    @Deprecated
    public static LazyValidator getConfigurer(Project project) {
        Project rootProject = project.getRootProject();
        //single configurer for the entire build, hooked up to the root project, for simplicity and speed
        //we don't want too many listeners that introduce blocking callbacks to Gradle internals

        LazyValidator validator = rootProject.getExtensions().findByType(LazyValidator.class);
        if (validator == null) {
            validator = new LazyValidator();
            rootProject.getExtensions().add(LazyValidator.class.getName(), validator);
            rootProject.getGradle().addListener(validator);
        }
        return validator;
    }

    /**
     * @deprecated please use {@link #lazyValidation(Task)} or {@link DeferredConfiguration#deferredConfiguration(Project, Action)}.
     */
    @Deprecated
    public void configureLazily(Task task, Runnable action) {
        actions.put(task, action);
    }

    /**
     * Fluent interface for stacking lazy validations
     */
    public static class LazyValidation {
        private List<RequiredValue> required = new LinkedList<RequiredValue>();

        /**
         * Ensures provided getter does not return null.
         * Throws {@link GradleException} with specified validationMessage if the getter yields null.
         */
        public LazyValidation notNull(String validationMessage, Callable getter) {
            required.add(new RequiredValue(validationMessage, getter));
            return this;
        }
    }

    private static class RequiredValue {
        final String validationMessage;
        final Callable getter;
        RequiredValue(String validationMessage, Callable getter) {
            this.validationMessage = validationMessage;
            this.getter = getter;
        }
    }

}