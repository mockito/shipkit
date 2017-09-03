package org.shipkit.internal.notes.improvements;

/**
 * Improvements based on some issue tracking system outside of the vcs.
 */
public class Improvements {

    /**
     * Fetches tickets from GitHub. Needs GitHub auth token.
     *
     * @param apiUrl GitHub API endpoint address, for example: https://api.github.com
     * @param repository the repository in format USER|COMPANY/REPO_NAME, for example: mockito/mockito
     * @param readOnlyAuthToken the GitHub auth token
     */
    public static ImprovementsProvider getGitHubProvider(String apiUrl, String repository, final String readOnlyAuthToken) {
        return new GitHubImprovementsProvider(apiUrl, repository, readOnlyAuthToken);
    }
}
