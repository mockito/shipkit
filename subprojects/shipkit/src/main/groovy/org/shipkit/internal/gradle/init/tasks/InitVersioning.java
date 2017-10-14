package org.shipkit.internal.gradle.init.tasks;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.init.InitVersioningTask;
import org.shipkit.internal.notes.util.IOUtil;

import java.io.File;

public class InitVersioning {

    private static final Logger LOG = Logging.getLogger(InitVersioningTask.class);

    private static final String FALLBACK_INITIAL_VERSION = "0.0.1";

    public void initVersioning(InitVersioningTask task) {
        File file = task.getVersionFile();
        if (file.exists()) {
            InitMessages.skipping(file.getAbsolutePath());
        } else {
            createVersionPropertiesFile(task.getProject(), file, task.getPath());
        }
    }

    private void createVersionPropertiesFile(Project project, File versionFile, String taskPath) {
        String version = determineVersion(project, versionFile, taskPath);

        String versionFileContent = "#Version of the produced binaries. This file is intended to be checked-in.\n"
            + "#It will be automatically bumped by release automation.\n"
            + "version=" + version + "\n";

        IOUtil.writeFile(versionFile, versionFileContent);
        InitMessages.generated(versionFile.getAbsolutePath());
        LOG.lifecycle("  Version number is now stored in '{}' file. Don't set 'version' in *.gradle file.", versionFile.getName());
    }

    private String determineVersion(Project project, File versionFile, String taskPath) {
        if ("unspecified".equals(project.getVersion())) {
            LOG.info("{} - 'project.version' is unspecified. Version will be set to '{}'. You can change it in '{}'.",
                taskPath, FALLBACK_INITIAL_VERSION, versionFile.getName());
            return FALLBACK_INITIAL_VERSION;
        } else {
            LOG.lifecycle("{} - configured '{}' version in '{}' file. Please remove 'version={}' setting from *.gradle file.",
                project.getVersion(), versionFile.getName(), project.getVersion());
            return project.getVersion().toString();
        }
    }
}
