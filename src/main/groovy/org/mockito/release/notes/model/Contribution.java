package org.mockito.release.notes.model;

import java.util.Collection;

/**
 * VCS contribution, author + commits
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
