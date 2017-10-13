package org.shipkit.internal.gradle.downstream.test;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.specs.Spec;
import org.shipkit.internal.gradle.release.UploadGistsPlugin;
import org.shipkit.internal.gradle.release.tasks.UploadGistsTask;
import org.shipkit.internal.gradle.util.TaskMaker;
import org.shipkit.internal.util.IncubatingWarning;

import java.io.File;

/**
 * This plugin tests your library end-to-end (e2e) using downstream projects.
 * It adds testDownstream task which can be configured to run tests for certain repositories
 * See {@link TestDownstreamTask} for more information
 *
 * Adds tasks:
 * <ul>
 *     <li>testDownstream - {@link TestDownstreamTask}</li>
 * </ul>
 */
public class TestDownstreamPlugin implements Plugin<Project> {

    public static final String TEST_DOWNSTREAM_TASK = "testDownstream";

    public void apply(final Project project) {
        IncubatingWarning.warn("downstream-testing plugin");

        project.getPlugins().apply(UploadGistsPlugin.class);

        TestDownstreamTask testDownstreamTask = TaskMaker.task(project, TEST_DOWNSTREAM_TASK, TestDownstreamTask.class, new Action<TestDownstreamTask>() {
            @Override
            public void execute(TestDownstreamTask task) {
                task.setDescription("Runs all downstream tests.");
            }
        });

        final UploadGistsTask uploadGistsTask = (UploadGistsTask) project.getTasks().findByName(UploadGistsPlugin.UPLOAD_GISTS_TASK);

        uploadGistsTask.setFilesToUpload(
            project
                .fileTree(testDownstreamTask.getLogDirectory())
                .filter(new Spec<File>() {
                    @Override
                    public boolean isSatisfiedBy(File file) {
                        return file.getName().endsWith(".log");
                    }
                })
        );

        testDownstreamTask.finalizedBy(uploadGistsTask);
    }

}
