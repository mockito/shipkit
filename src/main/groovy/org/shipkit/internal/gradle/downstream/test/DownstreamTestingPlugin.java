package org.shipkit.internal.gradle.downstream.test;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.internal.gradle.util.TaskMaker;
import org.shipkit.internal.util.IncubatingWarning;

/**
 * This plugin tests your library end-to-end (e2e) using downstream projects.
 * It adds downstreamTest task which can be configured to run tests for certain repositories
 * See {@link DownstreamTestTask} for more information
 *
 * Adds tasks:
 * <ul>
 *     <li>downstreamTest - {@link DownstreamTestTask}</li>
 * </ul>
 */
public class DownstreamTestingPlugin implements Plugin<Project> {

    public static final String DOWNSTREAM_TEST_TASK = "downstreamTest";

    public void apply(final Project project) {
        IncubatingWarning.warn("downstream-testing plugin");
        TaskMaker.task(project, DOWNSTREAM_TEST_TASK, DownstreamTestTask.class, new Action<DownstreamTestTask>() {
            @Override
            public void execute(DownstreamTestTask task) {
                task.setDescription("Runs all downstream tests.");
            }
        });
    }

}
