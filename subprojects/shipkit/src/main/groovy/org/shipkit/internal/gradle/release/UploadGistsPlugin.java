package org.shipkit.internal.gradle.release;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin;
import org.shipkit.internal.gradle.release.tasks.UploadGistsTask;
import org.shipkit.internal.gradle.util.TaskMaker;

/**
 * Adds a task for uploading files to Gist based on Ant pattern.
 * <p>
 *
 * Applies:
 * <ul>
 *     <li>{@link ShipkitConfigurationPlugin}</li>
 * </ul>
 *
 * Adds tasks:
 * <ul>
 *     <li>{@link UploadGistsTask}</li>
 * </ul>
 */
public class UploadGistsPlugin implements Plugin<Project> {

    @Override
    public void apply(final Project project) {
        final ShipkitConfiguration conf = project.getPlugins().apply(ShipkitConfigurationPlugin.class).getConfiguration();

        TaskMaker.task(project, "uploadGists", UploadGistsTask.class, new Action<UploadGistsTask>() {
            @Override
            public void execute(UploadGistsTask uploadGistsTask) {
                uploadGistsTask.setDescription("Uploads to Gist files provided by 'filesToUpload' task property.");
                uploadGistsTask.setGitHubApiUrl(conf.getGitHub().getApiUrl());
                uploadGistsTask.setGitHubWriteToken(conf.getLenient().getGitHub().getWriteAuthToken());
            }
        });
    }
}
