package org.mockito.release.notes.model;

import java.util.Collection;

/**
 * A set of contributions
 */
public interface ContributionSet {

    /**
     * All commits in given contribution set, spanning all authors
     */
    Collection<Commit> getAllCommits();

    /**
     * All tickets referenced in commit messages
     */
    Collection<String> getAllTickets();

    /**
     * All contributions in the set.
     */
    Collection<Contribution> getContributions();

    /**
     * All unique authors of this set of contributions.
     * Basically, it's the size of {@link #getContributions()}.
     */
    int getAuthorCount();
}
