package org.mockito.release.notes.format;

import org.mockito.release.notes.model.VersionNotesData;

/**
 * Formats version notes
 */
public interface SingleReleaseNotesFormatter {

    /**
     * Formats the version notes data
     */
    String formatVersion(VersionNotesData data);
}