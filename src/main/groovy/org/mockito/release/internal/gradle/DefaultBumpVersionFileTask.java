package org.mockito.release.internal.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;
import org.mockito.release.gradle.BumpVersionFileTask;
import org.mockito.release.internal.gradle.util.StringUtil;
import org.mockito.release.version.Version;
import org.mockito.release.version.VersionFile;

import java.io.File;

public class DefaultBumpVersionFileTask extends DefaultTask implements BumpVersionFileTask {

    private final static Logger LOG = Logging.getLogger(DefaultBumpVersionFileTask.class);

    private File versionFile;
    private boolean updateNotableVersions;

    @Override
    @InputFile
    public File getVersionFile() {
        return versionFile;
    }

    @Override
    public void setVersionFile(File versionFile) {
        this.versionFile = versionFile;
    }

    @Override
    public void setUpdateNotableVersions(boolean update) {
        this.updateNotableVersions = update;
    }

    @Override
    public boolean getUpdateNotableVersions() {
        return updateNotableVersions;
    }

    @TaskAction public void bumpVersionFile() {
        VersionFile versionFile = Version.versionFile(this.versionFile);
        String newVersion = versionFile.bumpVersion(updateNotableVersions);
        LOG.lifecycle("{} - updated version file '{}'\n" +
                "  - new version: {}\n" +
                "  - notable versions updated: {}\n" +
                "  - notable versions: {}",
                getPath(), getProject().relativePath(this.versionFile), newVersion, updateNotableVersions,
                StringUtil.join(versionFile.getNotableVersions(), ", "));
    }
}
