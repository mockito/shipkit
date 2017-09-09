package org.shipkit.internal.gradle.version.tasks;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.version.BumpVersionFileTask;
import org.shipkit.internal.version.Version;
import org.shipkit.version.VersionInfo;

public class BumpVersionFile {

    private final static Logger LOG = Logging.getLogger(BumpVersionFileTask.class);

    public void bumpVersionFile(BumpVersionFileTask task) {
        VersionInfo versionInfo = Version.versionInfo(task.getVersionFile());
        VersionInfo newVersion = versionInfo.bumpVersion();
        String versionFile = task.getVersionFile().getName();
        LOG.lifecycle(versionMessage(newVersion, versionFile, task.getPath()));
    }

    static String versionMessage(VersionInfo newVersion, String versionFile, String taskPath) {
        return taskPath + " - updated version file '" + versionFile + "'\n" +
            "  - new version: " + newVersion.getVersion() + "\n" +
            "  - previous version: " + newVersion.getPreviousVersion();
    }
}
