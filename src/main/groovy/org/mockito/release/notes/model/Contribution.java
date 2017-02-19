package org.mockito.release.notes.model;

import java.util.Collection;

/**
 * VCS contribution, author + all commits.
 * Contribution holds many commits and potentially many improvements by a single author.
 */
public interface Contribution {

    /**
     * Commits
     */
    Collection<Commit> getCommits();

    /**
     * The name of the author
     */
    String getAuthorName();
}
