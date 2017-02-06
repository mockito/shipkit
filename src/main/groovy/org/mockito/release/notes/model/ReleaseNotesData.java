package org.mockito.release.notes.model;

import java.util.Collection;
import java.util.Date;

/**
 * Contains all the information that is needed for release notes
 */
public interface ReleaseNotesData {

    /**
     * Version of the released software component
     */
    String getVersion();

    /**
     * Date of the release
     */
    Date getDate();

    /**
     * Contributions (authors and commits from VCS)
     */
    ContributionSet getContributions();

    /**
     * Improvements (issues, pull requests from issue tracker)
     */
    Collection<Improvement> getImprovements();
}
