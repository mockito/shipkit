package org.mockito.release.notes.format;

import org.mockito.release.notes.improvements.Improvement;
import org.mockito.release.notes.vcs.ContributionSet;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Formats release notes
 */
public interface ReleaseNotesFormatter {

    /**
     * Formats the release notes metadata
     */
    String formatNotes(String version, Date date, ContributionSet contributions,
                       Map<String, String> labels, Collection<Improvement> improvements);
}
