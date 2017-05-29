package org.shipkit.version;

/**
 * The file that contains version number
 */
public interface VersionInfo {

    /**
     * Version number
     */
    String getVersion();

    /**
     * Number of last released version
     */
    String getPreviousVersion();

    /**
     * Increments version number in the backing object (typically a file)
     * and returns incremented version info instance.
     */
    VersionInfo bumpVersion();
}
