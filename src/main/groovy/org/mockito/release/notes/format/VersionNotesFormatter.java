package org.mockito.release.notes.format;

import org.mockito.release.notes.model.VersionNotesData;

/**
 * Formats version notes
 */
public interface VersionNotesFormatter {

    /**
     * Formats the version notes data
     */
    String formatNotes(VersionNotesData data);
}
