package org.mockito.release.version;

import java.util.Collection;

/**
 * The file that contains version number
 * TODO rename to VersionInfo
 */
public interface VersionFile {

    /**
     * Version number
     */
    String getVersion();

    /**
     * Increments version number in the backing object (typically a file) and returns incremented value.
     *
     * @param updateNotable if true, the previous version will be included in the notable versions, too.
     */
    String bumpVersion(boolean updateNotable);

    /**
     * Returns notable versions
     */
    Collection<String> getNotableVersions();
}
