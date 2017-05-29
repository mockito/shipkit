package org.shipkit.notes.model;

import org.json.simple.Jsonable;

import java.io.Serializable;
import java.util.Collection;

/**
 * VCS change, a commit
 */
public interface Commit extends Jsonable, Serializable {

    /**
     * Commit identifier. For git it would be 'hash' (SHA-1)
     */
    String getCommitId();

    /**
     * Author identifier. For git it would be 'email'
     */
    String getAuthorEmail();

    /**
     * Author display name. For git it would be 'author'
     */
    String getAuthorName();

    /**
     * Commit message
     */
    String getMessage();

    /**
     * Tickets referenced by the commit. For example, jira issue ids or GitHub issue ids.
     */
    Collection<String> getTickets();
}
