package org.mockito.release.gradle;

import org.gradle.api.Task;

import java.io.File;

/**
 * Increments version in specified file.
 * The file is expected to have a version property declared, for example: "version=0.0.1"
 * If {@link #setUpdateNotableVersions(boolean)} is set to true
 * then the previous version will be added to notable versions, e.g. "notableVersions=1.0.0,1.5.0,2.0.0"
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

    /**
     * See {@link #getUpdateNotableVersions()}
     */
    void setUpdateNotableVersions(boolean update);

    /**
     * Whether to update notable versions by adding previous version to list
     */
    boolean getUpdateNotableVersions();
}