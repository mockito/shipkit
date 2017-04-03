package org.mockito.release.notes.model;

import java.io.Serializable;
import java.util.Collection;

/**
 * VCS contribution, author + all commits.
 * Contribution holds many commits and potentially many improvements by a single author.
 */
public interface Contribution extends Serializable{

    /**
     * Commits
     */
    Collection<Commit> getCommits();

    /**
     * The name of the author
     */
    String getAuthorName();
}
