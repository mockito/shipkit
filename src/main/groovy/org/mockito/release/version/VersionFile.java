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
     * Increments version number in the backing object (typically a file) and returns incremented value
     */
    String incrementVersion();

    /**
     * Returns notable versions
     */
    Collection<String> getNotableVersions();
}
