package org.shipkit.internal.notes.vcs;

import org.shipkit.internal.notes.model.ContributionSet;

/**
 * Knows the contributions
 */
public interface ContributionsProvider {

    /**
     * Provides contributions between specified revisions
     */
    ContributionSet getContributionsBetween(String fromRev, String toRev);
}
