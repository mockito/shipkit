package org.mockito.release.internal.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;
import org.mockito.release.gradle.notes.BumpVersionFileTask;
import org.mockito.release.version.Version;

import java.io.File;

public class DefaultBumpVersionFileTask extends DefaultTask implements BumpVersionFileTask {

    private final static Logger LOG = Logging.getLogger(DefaultBumpVersionFileTask.class);

    private File versionFile;

    @Override
    @InputFile
    public File getVersionFile() {
        return versionFile;
    }

    @Override
    public void setVersionFile(File versionFile) {
        this.versionFile = versionFile;
    }

    @TaskAction public void bumpVersion() {
        String newVersion = Version.versionFile(versionFile).incrementVersion();
        LOG.lifecycle("{} - bumped version to {} in file: '{}'.", getPath(), newVersion, getProject().relativePath(versionFile));
    }
}
