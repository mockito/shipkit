package org.mockito.release.version;

import java.io.File;

/**
 * Version utilities
 */
public class Version {

    /**
     * Provides instance of version file
     */
    public static VersionFile versionFile(File versionFile) {
        return DefaultVersionFile.fromFile(versionFile);
    }
}
