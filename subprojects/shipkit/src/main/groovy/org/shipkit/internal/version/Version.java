package org.shipkit.internal.version;

import org.shipkit.version.VersionInfo;

import java.io.File;

/**
 * Version utilities
 */
public class Version {

    /**
     * Provides instance of version information, version is loaded from file
     */
    public static VersionInfo versionInfo(File versionFile, boolean isSnapshot) {
        return DefaultVersionInfo.fromFile(versionFile, isSnapshot);
    }

    /**
     * Provides instance of version information, version has to be passed explicitly
     */
    public static VersionInfo defaultVersionInfo(File versionFile, String projectVersion, boolean isSnapshot) {
        return DefaultVersionInfo.fromString(versionFile, projectVersion, isSnapshot);
    }
}
