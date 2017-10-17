package org.shipkit.internal.gradle.downstream.test;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileTree;
import org.shipkit.internal.gradle.configuration.DeferredConfiguration;
import org.shipkit.internal.gradle.release.UploadGistsPlugin;
import org.shipkit.internal.gradle.release.tasks.UploadGistsTask;
import org.shipkit.internal.gradle.util.TaskMaker;
import org.shipkit.internal.util.IncubatingWarning;

import java.io.File;

/**
 * This plugin tests your library end-to-end (e2e) using downstream projects.
 * It adds testDownstream task which can be configured to run tests for certain repositories.
 * Output of each downstream test release task is saved to file which is uploaded to Gist
 * even if the task is not successful.
 * See {@link TestDownstreamTask} for more information
 *
 * Applies following plugins:
 * <ul>
 *     <li>{@link UploadGistsPlugin}</li>
 * </ul>
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

        uploadGistsTask.setFilesToUpload((ConfigurableFileTree)
            project
                .fileTree(testDownstreamTask.getLogDirectory())
                .include("*.log")
        );

        // it has to be deferred because TestDownstreamReleaseTask are added by user after applying this plugin
        DeferredConfiguration.deferredConfiguration(project, new Runnable() {
            @Override
            public void run() {
                project.getTasks().withType(TestDownstreamReleaseTask.class, new Action<TestDownstreamReleaseTask>() {
                    @Override
                    public void execute(TestDownstreamReleaseTask testDownstreamReleaseTask) {
                        testDownstreamReleaseTask.finalizedBy(uploadGistsTask);
                    }
                });
            }
        });
    }

}
