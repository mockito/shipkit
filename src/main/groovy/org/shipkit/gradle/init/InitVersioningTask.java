package org.shipkit.gradle.init;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.gradle.init.tasks.InitVersioning;

import java.io.File;

/**
 * Initializes versioning for the project by creating version file.
 * Typically it is "version.properties" file project's root directory.
 * If the file exists, this task does nothing.
 * Generated file is intended to be checked in.
 */
public class InitVersioningTask extends DefaultTask{

    @OutputFile private File versionFile;

    @TaskAction public void initVersioning() {
        new InitVersioning().initVersioning(this);
    }

    /**
     * Initial version file, intended to be checked in.
     * If the file exists, this task does nothing.
     */
    public File getVersionFile() {
        return versionFile;
    }

    /**
     * See {@link #getVersionFile()}
     */
    public void setVersionFile(File versionFile) {
        this.versionFile = versionFile;
    }
}
