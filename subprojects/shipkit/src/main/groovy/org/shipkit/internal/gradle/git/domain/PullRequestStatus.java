package org.shipkit.internal.gradle.git.domain;

/**
 * Enum representing list of possible results of status checks in pull request
 */
public enum PullRequestStatus {
    TIMEOUT, SUCCESS, NO_CHECK_DEFINED
}
