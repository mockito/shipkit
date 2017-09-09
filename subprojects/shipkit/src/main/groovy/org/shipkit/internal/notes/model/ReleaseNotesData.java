package org.shipkit.internal.notes.model;

import org.json.simple.Jsonable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * Contains all the information that is needed for release notes for single specific release (version).
 */
public interface ReleaseNotesData extends Jsonable, Serializable {

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

    /**
     * The vcs addressable tag of this version
     */
    String getVcsTag();

    /**
     * The vcs addressable tag of previous version
     */
    String getPreviousVersionVcsTag();
}
