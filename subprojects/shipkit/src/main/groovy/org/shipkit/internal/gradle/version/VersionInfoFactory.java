package org.shipkit.internal.gradle.version;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.internal.version.Version;
import org.shipkit.version.VersionInfo;

import java.io.File;
import java.util.List;

class VersionInfoFactory {

    private final static Logger LOG = Logging.getLogger(VersionInfoFactory.class);

    /**
     * Creates version info and logs the version we will be building
     */
    VersionInfo createVersionInfo(File versionFile, Object version, List<String> taskNames) {
        boolean isSnapshot = taskNames.contains("snapshot");
        VersionInfo info;
        if (versionFile.isFile()) {
            info = Version.versionInfo(versionFile, isSnapshot);
            LOG.lifecycle("  Building version '{}' (value loaded from '{}' file).", info.getVersion(), versionFile.getName());
        } else {
            info = Version.defaultVersionInfo(versionFile, version.toString(), isSnapshot);
            LOG.lifecycle("  Building version '{}'.", info.getVersion());
        }
        return info;
    }
}
