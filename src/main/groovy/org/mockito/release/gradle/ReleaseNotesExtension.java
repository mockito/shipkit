package org.mockito.release.gradle;

import java.io.File;
import java.util.Map;

/**
 * TODO: kill this API, figure out what to do with 'getPreviousVersion'
 */
@Deprecated
public interface ReleaseNotesExtension {

    /**
     * Returns previous version based on the release notes file.
     * It parses the first line of the release notes file to identify previously released version.
     */
    String getPreviousVersion();

    /**
     * Generates and returns incremental release notes text that can be appended to the release notes file.
     * @param version of the release to generate notes for
     */
    String getReleaseNotes(String version);

    /**
     * Generates incremental release notes content using {@link #getReleaseNotes(String)} )}
     * and appends it to the top of release notes file.
     * @param version of the release to generate notes for
     */
    void updateReleaseNotes(String version);

    /**
     * The file where the release notes are kept.
     */
    File getReleaseNotesFile();

    /**
     * The file where the release notes are kept.
     */
    void setReleaseNotesFile(File notesFile);

    /**
     * GitHub read only auth token for getting the issue links.
     */
    String getGitHubReadOnlyAuthToken();

    /**
     * GitHub read only auth token for getting the issue links.
     */
    void setGitHubReadOnlyAuthToken(String gitHubAuthToken);

    /**
     * The mapping of "GitHub label" to human readable and presentable name.
     * The order of labels is important and will influence the order in which groups of issues are generated in release notes.
     * Examples: ['java-9': 'Java 9 support', 'BDD': 'Behavior-Driven Development support']
     */
    Map<String, String> getGitHubLabelMapping();

    /**
     * See {@link #getGitHubLabelMapping()}.
     */
    void setGitHubLabelMapping(Map<String, String> gitHubLabelMapping);

    /**
     * GitHub repository, for example: "mockito/mockito"
     */
    String getGitHubRepository();

    /**
     * GitHub repository, for example: "mockito/mockito"
     */
    void setGitHubRepository(String gitHubRepository);
}
