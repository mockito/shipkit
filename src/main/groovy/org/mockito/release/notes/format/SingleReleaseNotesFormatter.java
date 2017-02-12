package org.mockito.release.notes.format;

import org.mockito.release.notes.model.ReleaseNotesData;

/**
 * Formats version notes
 */
public interface SingleReleaseNotesFormatter {

    /**
     * Formats the version notes data
     */
    String formatVersion(ReleaseNotesData data);
}