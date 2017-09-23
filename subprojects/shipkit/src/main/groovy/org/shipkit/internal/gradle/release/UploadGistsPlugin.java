package org.shipkit.internal.gradle.release;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin;
import org.shipkit.internal.gradle.release.tasks.UploadGistsTask;
import org.shipkit.internal.gradle.util.StringUtil;
import org.shipkit.internal.gradle.util.TaskMaker;

import java.util.Arrays;
import java.util.List;

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

    public static final String FILES_PATTERNS_PROPERTY = "filesPatterns";

    private List<String> filesPatterns;

    @Override
    public void apply(final Project project) {
        filesPatterns = extractLogDirPatternsFromProjProperty(project);
        final ShipkitConfiguration conf = project.getPlugins().apply(ShipkitConfigurationPlugin.class).getConfiguration();

        TaskMaker.task(project, "uploadGists", UploadGistsTask.class, new Action<UploadGistsTask>() {
            @Override
            public void execute(UploadGistsTask uploadGistsTask) {
                uploadGistsTask.setDescription("Uploads files matching patterns in property '" + FILES_PATTERNS_PROPERTY + "', to Gist");
                uploadGistsTask.setFilesPatterns(filesPatterns);
                uploadGistsTask.setRootDir(project.getRootDir().getAbsolutePath());
                uploadGistsTask.setGitHubApiUrl(conf.getGitHub().getApiUrl());
                uploadGistsTask.setGitHubWriteToken(conf.getLenient().getGitHub().getWriteAuthToken());
            }
        });
    }

    private List<String> extractLogDirPatternsFromProjProperty(Project project) {
        String property = (String) project.findProperty(FILES_PATTERNS_PROPERTY);
        if (StringUtil.isEmpty(property)) {
            return null; // return null so that UploadGistsTask fails if it is executed (because of @Input)
        }
        return Arrays.asList(property.split(","));
    }

    List<String> getFilesPatterns() {
        return filesPatterns;
    }
}
