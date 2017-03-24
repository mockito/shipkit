package org.mockito.release.gradle;

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
}
