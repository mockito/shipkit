package org.mockito.release.notes.contributors;

import java.io.File;

/**
 * Contributors based on some system outside of the vcs.
 */
public class Contributors {

    /**
     * Fetches contributors from GitHub. Needs GitHub auth token.
     *
     * @param repository name of GitHub repository, for example: "mockito/mockito"
     * @param readOnlyAuthToken the GitHub auth token
     */
    public static GitHubContributorsProvider getGitHubContributorsProvider(String repository, String readOnlyAuthToken) {
        return new GitHubContributorsProvider(repository, readOnlyAuthToken);
    }

    /**
     * Return Json serializer for last last contributions
     * @param contributorsFile file where last contributions are stored
     * @return instance of {@link ContributorsSerializer}
     */
    public static ContributorsSerializer getLastContributorsSerializer(File contributorsFile) {
        return new ContributorsSerializer(contributorsFile);
    }

    /**
     * Return Json serializer for all project contributors
     * @return instance of {@link AllContributorsSerializer}
     */
    public static AllContributorsSerializer getAllContributorsSerializer() {
        return new AllContributorsSerializer();
    }

}
