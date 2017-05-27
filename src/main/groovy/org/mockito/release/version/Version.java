package org.mockito.release.version;

import java.io.File;
import java.util.LinkedList;

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

    public static VersionInfo defaultVersionInfo(File versionFile, String projectVersion){
        return new DefaultVersionInfo(versionFile, projectVersion, new LinkedList<String>(), null);
    }
}
