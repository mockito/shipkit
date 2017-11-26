package org.shipkit.internal.gradle.downstream.test;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin;
import org.shipkit.internal.gradle.release.CiContext;
import org.shipkit.internal.gradle.release.UploadGistsPlugin;
import org.shipkit.internal.gradle.release.tasks.UploadGistsTask;
import org.shipkit.internal.gradle.util.StringUtil;
import org.shipkit.internal.gradle.util.TaskMaker;
import org.shipkit.internal.util.IncubatingWarning;

import javax.inject.Inject;
import java.io.File;

/**
 * This plugin tests your library end-to-end (e2e) using downstream projects.
 * It adds testDownstream task which can be configured to run tests for certain repositories.
 * Output of each downstream test release task is saved to file which is uploaded to Gist
 * even if the task is not successful.
 * Uploading of Gists is disabled when build is outside of CI environment or GH_WRITE_TOKEN is not set.
 * See {@link TestDownstreamTask} for more information
 *
 * Applies following plugins:
 * <ul>
 *     <li>{@link UploadGistsPlugin}</li>
 *     <li>{@link ShipkitConfigurationPlugin}</li>
 * </ul>
 *
 * Adds tasks:
 * <ul>
 *     <li>testDownstream - {@link TestDownstreamTask}</li>
 * </ul>
 */
public class TestDownstreamPlugin implements Plugin<Project> {
    private static final Logger LOG = Logging.getLogger(TestDownstreamPlugin.class);
    public static final String TEST_DOWNSTREAM_TASK = "testDownstream";
    private CiContext ciContext;

    @Inject
    public TestDownstreamPlugin() {
        ciContext = new CiContext();
    }

    TestDownstreamPlugin(CiContext ciContext) {
        this.ciContext = ciContext;
    }

    public void apply(final Project project) {
        IncubatingWarning.warn("downstream-testing plugin");

        project.getPlugins().apply(UploadGistsPlugin.class);
        final ShipkitConfiguration conf = project.getPlugins().apply(ShipkitConfigurationPlugin.class).getConfiguration();

        final File logsDirectory = project.getBuildDir();

        final UploadGistsTask uploadGistsTask = (UploadGistsTask) project.getTasks().findByName(UploadGistsPlugin.UPLOAD_GISTS_TASK);

        uploadGistsTask.setFilesToUpload((ConfigurableFileTree)
            project
                .fileTree(logsDirectory)
                .include("*.log")
        );

        boolean shouldUploadLogs = ciContext.isCiBuild() && !StringUtil.isEmpty(conf.getLenient().getGitHub().getWriteAuthToken());

        if (shouldUploadLogs) {
            LOG.debug("  Skipping UploadGists task execution -> test-downstream plugin will not send logs to Gist (setting uploadGists.enabled=false)." +
                "  It's only enabled when build is run in CI environment and GH_WRITE_TOKEN env property is set.");
        }
        uploadGistsTask.setEnabled(shouldUploadLogs);

        TaskMaker.task(project, TEST_DOWNSTREAM_TASK, TestDownstreamTask.class, new Action<TestDownstreamTask>() {
            @Override
            public void execute(TestDownstreamTask task) {
                task.setDescription("Runs all downstream tests.");
                task.setLogsDirectory(logsDirectory);
                task.setUploadGistsTask(uploadGistsTask);
                task.setGitHubUrl(conf.getGitHub().getUrl());
            }
        });
    }
}
