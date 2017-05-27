package org.mockito.release.internal.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;
import org.mockito.release.notes.util.IOUtil;

import java.io.File;

public class InitVersioningTask extends DefaultTask{

    private static final Logger LOG = Logging.getLogger(InitVersioningTask.class);

    private static final String FALLBACK_INITIAL_VERSION = "0.0.1";

    private File versionFile;

    @TaskAction public void initVersioning(){
        if(versionFile.exists()){
            LOG.lifecycle("  File version.properties already exists, nothing to create.");
        } else{
            createVersionPropertiesFile(getProject(), versionFile);
        }
    }

    private void createVersionPropertiesFile(Project project, File versionFile) {
        LOG.lifecycle("  Required file version.properties doesn't exist. Creating it automatically. Remember about checking it into VCS!");
        LOG.lifecycle("  You shouldn't configure project.version in build.gradle anymore. Version from version.properties will be used instead.");

        String version = determineVersion(project);

        String versionFileContent = "#Version of the produced binaries. This file is intended to be checked-in.\n"
                + "#It will be automatically bumped by release automation.\n"
                + "version=" + version + "\n";

        IOUtil.writeFile(versionFile, versionFileContent);
    }

    private String determineVersion(Project project){
        if("unspecified".equals(project.getVersion()) ){
            LOG.lifecycle("  BEWARE! Project.version is unspecified. Version will be set to {}. You can change it manually in version.properties.", FALLBACK_INITIAL_VERSION);
            return FALLBACK_INITIAL_VERSION;
        } else{
            LOG.lifecycle("  Initial project version in version.properties set to {} (taken from project.version property).", project.getVersion());
            return project.getVersion().toString();
        }
    }

    public File getVersionFile() {
        return versionFile;
    }

    public void setVersionFile(File versionFile) {
        this.versionFile = versionFile;
    }
}
