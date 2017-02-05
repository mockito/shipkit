package org.mockito.release.notes.format;

import org.mockito.release.notes.model.ReleaseNotesData;
import org.mockito.release.notes.model.ReleaseNotesFormat;

/**
 * Formats release notes
 */
public interface ReleaseNotesFormatter {

    /**
     * Formats the release notes data
     */
    String formatNotes(ReleaseNotesData data, ReleaseNotesFormat format);
}
