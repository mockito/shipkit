package org.mockito.release.notes.contributors;

/**
 * Contributors based on some system outside of the vcs.
 */
public class Contributors {

    private static final String CONTRIBUTORS_FILE_PATH = "/contributors-%s-%s.json";

    /**
     * Fetches contribiutors from GitHub. Needs GitHub auth token.
     *
     * @param repository name of GitHub repository, for example: "mockito/mockito"
     * @param authToken the GitHub auth token
     */
    public static GitHubContributorsProvider getGitHubContibutorsProvider(String repository, String authToken) {
        return new GitHubContributorsProvider(repository, authToken);
    }

    public static String getContributorsFileName(String buildDir, String fromRev, String toRevision) {
        return buildDir + String.format(CONTRIBUTORS_FILE_PATH, fromRev, toRevision);
    }
}
