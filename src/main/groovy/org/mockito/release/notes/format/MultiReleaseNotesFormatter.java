package org.mockito.release.notes.format;

import org.mockito.release.notes.model.ReleaseNotesData;

/**
 * Formats release notes across multiple releases
 */
public interface MultiReleaseNotesFormatter {

    /**
     * Formats release notes data for all releases
     */
    String formatReleaseNotes(Iterable<ReleaseNotesData> data);
}
