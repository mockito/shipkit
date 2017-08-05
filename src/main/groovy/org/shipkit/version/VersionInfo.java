package org.shipkit.version;

/**
 * Version information, by default backed by 'version.properties' file.
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
