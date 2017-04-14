package org.mockito.release.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;
import org.mockito.release.internal.gradle.util.StringUtil;
import org.mockito.release.version.Version;
import org.mockito.release.version.VersionInfo;

import java.io.File;

/**
 * Increments version in specified file.
 * The file is expected to have a version property declared, for example: "version=0.0.1"
 * If {@link #setUpdateNotableVersions(boolean)} is set to true
 * then the previous version will be added to notable versions, e.g. "notableVersions=1.0.0,1.5.0,2.0.0"
 */
public class BumpVersionFileTask extends DefaultTask {

    private final static Logger LOG = Logging.getLogger(BumpVersionFileTask.class);

    private File versionFile;
    private boolean updateNotableVersions;

    /**
     * File that contains version number information, for example: "version=0.0.1"
     */
    @InputFile
    public File getVersionFile() {
        return versionFile;
    }

    /**
     * See {@link #getVersionFile()}
     */
    public void setVersionFile(File versionFile) {
        this.versionFile = versionFile;
    }

    /**
     * Whether to update notable versions by adding previous version to notable versions list
     */
    public boolean getUpdateNotableVersions() {
        return updateNotableVersions;
    }

    /**
     * See {@link #getUpdateNotableVersions()}
     */
    public void setUpdateNotableVersions(boolean update) {
        this.updateNotableVersions = update;
    }

    /**
     * See {@link BumpVersionFileTask}
     */
    @TaskAction public void bumpVersionFile() {
        VersionInfo versionInfo = Version.versionInfo(this.versionFile);
        VersionInfo newVersion = versionInfo.bumpVersion(updateNotableVersions);
        LOG.lifecycle("{} - updated version file '{}'\n" +
                "  - new version: {}\n" +
                "  - notable versions updated: {}\n" +
                "  - notable versions: {}",
                getPath(), getProject().relativePath(this.versionFile), newVersion, updateNotableVersions,
                StringUtil.join(versionInfo.getNotableVersions(), ", "));
    }
}
