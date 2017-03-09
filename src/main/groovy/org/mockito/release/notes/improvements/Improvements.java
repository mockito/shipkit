package org.mockito.release.notes.improvements;

/**
 * Improvements based on some issue tracking system outside of the vcs.
 */
public class Improvements {

    /**
     * Fetches tickets from GitHub. Needs GitHub auth token.
     *
     * @param repository the repository in format USER|COMPANY/REPO_NAME, for example: mockito/mockito
     * @param authToken the GitHub auth token
     */
    public static ImprovementsProvider getGitHubProvider(String repository, final String authToken) {
        return new GitHubImprovementsProvider(repository, authToken);
    }
}
