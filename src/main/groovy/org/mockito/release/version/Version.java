package org.mockito.release.version;

import java.io.File;

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
}
