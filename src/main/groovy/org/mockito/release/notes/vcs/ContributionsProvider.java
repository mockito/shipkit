package org.mockito.release.notes.vcs;

import org.mockito.release.notes.model.ContributionSet;

/**
 * Knows the contributions
 */
public interface ContributionsProvider {

    /**
     * Provides contributions between specified versions
     */
    ContributionSet getContributionsBetween(String fromRev, String toRev);
}
