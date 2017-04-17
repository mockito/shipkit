package org.mockito.release.notes;

import org.mockito.release.notes.versions.PreviousVersion;
import org.mockito.release.notes.versions.Versions;

import java.io.File;

/**
 * Release notes services
 */
public class Notes {

    /**
     * Release notes build based on git and GitHub.
     * @param workDir working directory for executing external processes like 'git log'
     * @param buildDir build directory
     * @param repository GitHub repository, for example "mockito/mockito"
     * @param gitHubReadOnlyAuthToken GitHub auth token, read-only please!
     */
    public static NotesBuilder gitHubNotesBuilder(File workDir, File buildDir, String repository, String gitHubReadOnlyAuthToken) {
        return new GitNotesBuilder(workDir, buildDir, repository, gitHubReadOnlyAuthToken);
    }

    /**
     * Provides previous version information based on the release notes content file
     */
    public static PreviousVersion previousVersion(String releaseNotesContent) {
        return Versions.previousFromNotesContent(releaseNotesContent);
    }
}
