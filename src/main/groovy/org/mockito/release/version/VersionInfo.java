package org.mockito.release.version;

import java.util.Collection;

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
     *
     * @param updateNotable if true, the previous version will be included in the notable versions, too.
     */
    VersionInfo bumpVersion(boolean updateNotable);

    /**
     * Returns notable versions
     */
    Collection<String> getNotableVersions();

    /**
     * Informs if the current version is a notable release
     */
    boolean isNotableRelease();
}
