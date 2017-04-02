package org.mockito.release.notes.vcs;

import java.util.Date;

/**
 * Contains information about released version
 */
public interface ReleasedVersion {

    /**
     * Version number
     */
    String getVersion();

    /**
     * Release date
     */
    Date getDate();

    /**
     * vcs addressable revision (tag)
     */
    String getRev();

    /**
     * Nullable, previous revision (tag)
     */
    String getPreviousRev();
}
