package org.shipkit.internal.notes.model;

import org.json.simple.Jsonable;

import java.io.Serializable;
import java.util.Collection;

/**
 * VCS contribution, author + all commits.
 * Contribution holds many commits and potentially many improvements by a single author.
 */
public interface Contribution extends Jsonable, Serializable {

    /**
     * Commits
     */
    Collection<Commit> getCommits();

    /**
     * The name of the author
     */
    String getAuthorName();
}
