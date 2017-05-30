package org.shipkit.internal.version;

import java.io.File;

/**
 * Version utilities
 */
public class Version {

    //TODO move entire "org.mockito.release.version" -> "org.mockito.release.internal.version"

    /**
     * Provides instance of version information
     */
    public static VersionInfo versionInfo(File versionFile) {
        return DefaultVersionInfo.fromFile(versionFile);
    }
}
