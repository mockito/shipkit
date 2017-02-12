package org.mockito.release.notes.format;

import org.mockito.release.notes.model.VersionNotesData;

/**
 * Formats release notes across multiple releases
 */
public interface MultiReleaseNotesFormatter {

    /**
     * Formats release notes data for all releases
     */
    String formatReleaseNotes(Iterable<VersionNotesData> data);
}
