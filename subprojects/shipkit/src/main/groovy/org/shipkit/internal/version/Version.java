package org.shipkit.internal.version;

import org.shipkit.version.VersionInfo;

import java.io.File;
import java.util.LinkedList;

/**
 * Version utilities
 */
public class Version {

    /**
     * Provides instance of version information
     */
    public static VersionInfo versionInfo(File versionFile) {
        return DefaultVersionInfo.fromFile(versionFile);
    }

    public static VersionInfo defaultVersionInfo(File versionFile, String projectVersion) {
        return new DefaultVersionInfo(versionFile, projectVersion, new LinkedList<String>(), null);
    }
}
