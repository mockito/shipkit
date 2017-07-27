package org.shipkit.gradle.version;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.gradle.version.tasks.BumpVersionFile;

import java.io.File;

/**
 * Increments version in specified file.
 * The file is expected to have a version property declared, for example: "version=0.0.1"
 */
public class BumpVersionFileTask extends DefaultTask {

    @InputFile private File versionFile;

    /**
     * See {@link BumpVersionFileTask}
     */
    @TaskAction
    public void bumpVersionFile() {
        new BumpVersionFile().bumpVersionFile(this);
    }

    /**
     * File that contains version number information, for example: "version=0.0.1"
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
