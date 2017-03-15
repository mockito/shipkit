package org.mockito.release.notes.contributors;

/**
 * Contributors based on some system outside of the vcs.
 */
public class Contributors {

    /**
     * Fetches contribiutors from GitHub. Needs GitHub auth token.
     *
     * @param authToken the GitHub auth token
     */
    public static GitHubContributorsProvider getGitHubContibutorsProvider(String authToken) {
        return new GitHubContributorsProvider(authToken);
    }
}
