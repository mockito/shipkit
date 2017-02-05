package org.mockito.release.notes.format;

import org.mockito.release.notes.model.ReleaseNotesData;

/**
 * Formats release notes
 */
public interface ReleaseNotesFormatter {

    /**
     * Formats the release notes data
     */
    String formatNotes(ReleaseNotesData data);
}
