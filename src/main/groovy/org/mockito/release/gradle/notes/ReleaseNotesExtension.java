package org.mockito.release.gradle.notes;

import java.io.File;
import java.util.Map;

/**
 * This extension object is added by {@link ReleaseNotesPlugin}. Example configuration:
 *
 * <pre>
 *  notes {
 *    notesFile = file("docs/release-notes.md")
 *    gitHubAuthToken = "secret"
 *    gitHubLabelMappings = ['java-9': 'Java 9 support', 'BDD': 'Behavior-Driven Development support']
 *  }
 * </pre>
 */
public interface ReleaseNotesExtension {

    /**
     * Returns previous version based on the release notes file.
     * It parses the first line of the release notes file to identify previously released version.
     */
    String getPreviousVersion();

    /**
     * Generates and returns incremental release notes text that can be appended to the release notes file.
     */
    String getReleaseNotes();

    /**
     * Generates incremental release notes content using {@link #getReleaseNotes()}
     * and appends it to the top of release notes file.
     */
    void updateReleaseNotes();

    /**
     * The file where the release notes are kept.
     */
    File getNotesFile();

    /**
     * The file where the release notes are kept.
     */
    void setNotesFile(File notesFile);

    /**
     * GitHub read only auth token for getting the issue links.
     */
    String getGitHubAuthToken();

    /**
     * TODO rename to 'gitHubReadOnlyAuthToken' so that it is clear that it is read only and we don't have to document it everywhere
     * GitHub read only auth token for getting the issue links.
     */
    void setGitHubAuthToken(String gitHubAuthToken);

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
}
