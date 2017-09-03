package org.shipkit.internal.notes.contributors.github;

import org.shipkit.internal.notes.contributors.ContributorsSerializer;

import java.io.File;

/**
 * Contributors based on some system outside of the vcs.
 */
public class Contributors {

    /**
     * Fetches contributors from GitHub. Needs GitHub auth token.
     *
     * @param apiUrl address of GitHub api endpoint, for example: "https://api.github.com"
     * @param repository name of GitHub repository, for example: "mockito/mockito"
     * @param readOnlyAuthToken the GitHub auth token
     */
    public static GitHubContributorsProvider getGitHubContributorsProvider(String apiUrl, String repository, String readOnlyAuthToken) {
        return new GitHubContributorsProvider(apiUrl, repository, readOnlyAuthToken);
    }

    /**
     * Return Json serializer for last last contributions
     * @param contributorsFile file where last contributions are stored
     * @return instance of {@link ContributorsSerializer}
     */
    public static ContributorsSerializer getLastContributorsSerializer(File contributorsFile) {
        return new ContributorsSerializer(contributorsFile);
    }
}
