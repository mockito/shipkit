package org.mockito.release.notes.format;

import org.mockito.release.notes.model.ReleaseNotesData;

import java.util.Collection;

/**
 * Formats release notes across multiple releases
 */
public interface MultiReleaseNotesFormatter {

    /**
     * Formats release notes data for all releases
     */
    String formatReleaseNotes(Collection<ReleaseNotesData> data);
}
