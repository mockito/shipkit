package org.mockito.release.gradle.notes;

import org.gradle.api.Task;

import java.io.File;

/**
 * Increments version in specified file.
 * The file is expected to have a version property declared, for example: "version=0.0.1"
 */
public interface BumpVersionFileTask extends Task {

    /**
     * File that contains version number information, for example: "version=0.0.1"
     */
    File getVersionFile();

    /**
     * See {@link #getVersionFile()}
     */
    void setVersionFile(File versionFile);

}