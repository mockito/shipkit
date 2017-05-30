package org.shipkit.notes.vcs;

import org.shipkit.notes.model.ContributionSet;

/**
 * Knows the contributions
 */
public interface ContributionsProvider {

    /**
     * Provides contributions between specified revisions
     */
    ContributionSet getContributionsBetween(String fromRev, String toRev);
}
