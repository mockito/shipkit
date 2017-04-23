package org.mockito.release.notes.contributors;

import java.io.File;

/**
 * Contributors based on some system outside of the vcs.
 */
public class Contributors {

    private static final String LAST_CONTRIBUTORS_FILE_PATH = "/contributors/contributors-%s-%s.json";
    private static final String ALL_PROJECT_CONTRIBUTORS_FILE_PATH = "/contributors/project-contributors.json";

    /**
     * Fetches contribiutors from GitHub. Needs GitHub auth token.
     *
     * @param repository name of GitHub repository, for example: "mockito/mockito"
     * @param readOnlyAuthToken the GitHub auth token
     */
    public static GitHubContributorsProvider getGitHubContibutorsProvider(String repository, String readOnlyAuthToken) {
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
     * Generate file path where all project contributors are stored.
     * @param buildDir project build dir
     * @return file path
     */
    public static String getAllProjectContributorsFileName(String buildDir) {
        return buildDir + ALL_PROJECT_CONTRIBUTORS_FILE_PATH;
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
