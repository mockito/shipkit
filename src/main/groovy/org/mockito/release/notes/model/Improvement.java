package org.mockito.release.notes.model;

import java.util.Collection;

/**
 * Improvement tracked by given issue/bug tracker.
 * Example: an improvement corresponds to a JIRA ticket or a GitHub issue/pull request.
 */
public interface Improvement {

    /**
     * Identifier of the improvement. In case of GitHub it is a number.
     */
    Long getId();

    /**
     * Title of the improvement. In case of GitHub it is the "title"
     */
    String getTitle();

    /**
     * The link to the improvement. The target page contains the details of the improvement.
     * In case of GitHub, it is the link to the issue/pull request in GitHub.
     */
    String getUrl();

    /**
     * Labels of this improvement. In case of GitHub, those are labels attached to the issue/pull request.
     */
    Collection<String> getLabels();
}
