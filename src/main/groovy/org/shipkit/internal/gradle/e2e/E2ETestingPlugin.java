package org.shipkit.internal.gradle.e2e;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.internal.gradle.util.TaskMaker;

/**
 * This plugin tests your library end-to-end (e2e) using client projects.
 * It adds e2eTest task which can be configured to run tests for certain repositories
 * See {@link E2ETestTask} for more information
 *
 * Adds tasks:
 * <ul>
 *     <li>e2eTest - {@link E2ETestTask}</li>
 * </ul>
 */
public class E2ETestingPlugin implements Plugin<Project> {

    public static final String E2E_TEST_TASK = "e2eTest";

    public void apply(final Project project) {
        TaskMaker.task(project, E2E_TEST_TASK, E2ETestTask.class, new Action<E2ETestTask>() {
            @Override
            public void execute(E2ETestTask task) {
                task.setDescription("Runs all e2e tests.");
            }
        });
    }

}
