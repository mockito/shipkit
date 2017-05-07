package org.mockito.release.notes.contributors;

import java.io.File;

/**
 * Contributors based on some system outside of the vcs.
 */
public class Contributors {

    private static final String LAST_CONTRIBUTORS_FILE_PATH = "/contributors/contributors-%s-%s.json";

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
     * Generate file path where last contributors are stored.
     * @param buildDir project build dir
     * @param fromRev from revision or tag
     * @param toRevision end revision or 'HEAD'
     * @return file path
     */
    public static String getLastContributorsFileName(String buildDir, String fromRev, String toRevision) {
        return buildDir + String.format(LAST_CONTRIBUTORS_FILE_PATH, fromRev, toRevision);
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
     * @param contributorsFile file where all project contributions are stored
     * @return instance of {@link AllContributorsSerializer}
     */
    public static AllContributorsSerializer getAllContributorsSerializer(File contributorsFile) {
        return new AllContributorsSerializer(contributorsFile);
    }

}
