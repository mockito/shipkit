package org.mockito.release.internal.gradle;

import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.mockito.release.internal.gradle.util.FileUtil;
import org.mockito.release.notes.Notes;
import org.mockito.release.notes.NotesBuilder;
import org.mockito.release.notes.format.MultiReleaseNotesFormatter;
import org.mockito.release.notes.format.ReleaseNotesFormatters;
import org.mockito.release.notes.generator.ReleaseNotesGenerator;
import org.mockito.release.notes.generator.ReleaseNotesGenerators;
import org.mockito.release.notes.model.ReleaseNotesData;

import java.io.File;
import java.util.*;

public class DefaultReleaseNotesExtension {

    private static final Logger LOG = Logging.getLogger(DefaultReleaseNotesExtension.class);

    private File releaseNotesFile;
    private String gitHubAuthToken;
    private String gitHubRepository;
    private Map<String, String> gitHubLabelMapping = new LinkedHashMap<String, String>();

    private final File workDir;

    DefaultReleaseNotesExtension(File workDir) {
        this.workDir = workDir;
    }

    void assertConfigured() {
        //TODO SF unit test coverage
        if (releaseNotesFile == null || !releaseNotesFile.isFile()) {
            throw new GradleException("'notesFile' must be configured and the file must be present.");
        }

        if (gitHubAuthToken == null || gitHubAuthToken.trim().isEmpty()) {
            throw new GradleException("'gitHubAuthToken' must be configured.");
        }

        if (gitHubRepository == null || gitHubRepository.trim().isEmpty()) {
            throw new GradleException("'gitHubRepository' must be configured.");
        }
    }

    /**
     * Returns previous version based on the release notes file.
     * It parses the first line of the release notes file to identify previously released version.
     */
    private String getPreviousVersion() {
        //TODO this is really awkward method.
        // We should not be reading previous version from release notes file
        // We should either not read it at all (e.g. write the impl so that it does not require the previous version)
        // or store previous release version in the 'version.properties' file.
        assertConfigured();
        String firstLine = FileUtil.firstLine(releaseNotesFile);
        return Notes.previousVersion(firstLine).getPreviousVersion();
    }

    /**
     * Generates and returns incremental release notes text that can be appended to the release notes file.
     * @param version of the release to generate notes for
     */
    String getReleaseNotes(String version) {
        assertConfigured();
        LOG.lifecycle("Building new release notes based on {}", releaseNotesFile);
        NotesBuilder builder = Notes.gitHubNotesBuilder(workDir, gitHubRepository, gitHubAuthToken);
        String prev = "v" + getPreviousVersion();
        String current = "HEAD";
        LOG.lifecycle("Building notes for revisions: {} -> {}", prev, current);
        String newContent = builder.buildNotes(version, prev, current, gitHubLabelMapping);
        return newContent;
    }

    /**
     * Generates incremental release notes content using {@link #getReleaseNotes(String)} )}
     * and appends it to the top of release notes file.
     * @param version of the release to generate notes for
     */
    void updateReleaseNotes(String version) {
        String newContent = getReleaseNotes(version);
        FileUtil.appendToTop(newContent, releaseNotesFile);
        LOG.lifecycle("Successfully updated release notes!");
    }

    public String getCompleteReleaseNotes() {
        //TODO we should start building complete release notes instead of incremental ones
        ReleaseNotesGenerator generator = ReleaseNotesGenerators.releaseNotesGenerator(workDir, gitHubRepository, gitHubAuthToken);
        Collection<ReleaseNotesData> releaseNotes = generator.generateReleaseNotesData(null, new ArrayList<String>(Arrays.asList("2.7.5", "2.7.4", "2.7.3")), "v", new ArrayList<String>(), true);
        MultiReleaseNotesFormatter formatter = ReleaseNotesFormatters.detailedFormatter("Detailed release notes:\n\n", gitHubLabelMapping, "https://github.com/mockito/mockito/compare/{0}...{1}");
        return formatter.formatReleaseNotes(releaseNotes);
    }

    void setReleaseNotesFile(File file) {
        this.releaseNotesFile = file;
    }

    void setGitHubReadOnlyAuthToken(String gitHubAuthToken) {
        this.gitHubAuthToken = gitHubAuthToken;
    }

    void setGitHubLabelMapping(Map<String, String> gitHubLabelMapping) {
        this.gitHubLabelMapping = gitHubLabelMapping;
    }

    void setGitHubRepository(String gitHubRepository) {
        this.gitHubRepository = gitHubRepository;
    }
}
