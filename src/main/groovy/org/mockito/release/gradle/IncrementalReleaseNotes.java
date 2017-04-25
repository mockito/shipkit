package org.mockito.release.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.*;
import org.mockito.release.internal.gradle.util.FileUtil;
import org.mockito.release.notes.Notes;
import org.mockito.release.notes.NotesBuilder;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generates incremental, detailed release notes text.
 * that can be appended to the release notes file.
 */
public abstract class IncrementalReleaseNotes extends DefaultTask {

    private static final Logger LOG = Logging.getLogger(IncrementalReleaseNotes.class);

    private File releaseNotesFile;
    private String gitHubReadOnlyAuthToken;
    private String gitHubRepository;
    private Map<String, String> gitHubLabelMapping = new LinkedHashMap<String, String>();

    /**
     * Release notes file this task operates on.
     */
    @InputFile
    public File getReleaseNotesFile() {
        return releaseNotesFile;
    }

    /**
     * See {@link #getReleaseNotesFile()}
     */
    public void setReleaseNotesFile(File releaseNotesFile) {
        this.releaseNotesFile = releaseNotesFile;
    }

    /**
     * GitHub read only authentication token needed for loading issue information from GitHub.
     */
    @Input
    public String getGitHubReadOnlyAuthToken() {
        return gitHubReadOnlyAuthToken;
    }

    /**
     * See {@link #getGitHubReadOnlyAuthToken()}
     */
    public void setGitHubReadOnlyAuthToken(String readOnlyToken) {
        this.gitHubReadOnlyAuthToken = readOnlyToken;
    }

    /**
     * Name of the GitHub repository in format "user|org/repository",
     * for example: "mockito/mockito"
     */
    @Input
    public String getGitHubRepository() {
        return gitHubRepository;
    }

    /**
     * See {@link #getGitHubRepository()}
     */
    public void setGitHubRepository(String gitHubRepository) {
        this.gitHubRepository = gitHubRepository;
    }

    /**
     * Issue tracker label mappings.
     * The mapping of "GitHub label" to human readable and presentable name.
     * The order of labels is important and will influence the order
     * in which groups of issues are generated in release notes.
     * Examples: ['java-9': 'Java 9 support', 'BDD': 'Behavior-Driven Development support']
     */
    @Input
    @Optional
    public Map<String, String> getGitHubLabelMapping() {
        return gitHubLabelMapping;
    }

    /**
     * See {@link #getGitHubLabelMapping()}
     */
    public void setGitHubLabelMapping(Map<String, String> gitHubLabelMapping) {
        this.gitHubLabelMapping = gitHubLabelMapping;
    }

    private void assertConfigured() {
        //TODO SF unit test coverage
        if (releaseNotesFile == null || !releaseNotesFile.isFile()) {
            throw new GradleException("'" + this.getPath() + ".releaseNotesFile' must be configured and the file must be present.");
        }

        if (gitHubReadOnlyAuthToken == null || gitHubReadOnlyAuthToken.trim().isEmpty()) {
            throw new GradleException("'" + this.getPath() + ".gitHubReadOnlyToken' must be configured.");
        }

        if (gitHubRepository == null || gitHubRepository.trim().isEmpty()) {
            throw new GradleException("'" + this.getPath() + "gitHubRepository' must be configured.");
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
     * Generates new incremental content of the release notes.
     */
    protected String getNewContent() {
        assertConfigured();
        LOG.lifecycle("  Building new release notes based on {}", releaseNotesFile);
        NotesBuilder builder = Notes.gitHubNotesBuilder(
                this.getProject().getProjectDir(), this.getProject().getBuildDir(),
                gitHubRepository, gitHubReadOnlyAuthToken);
        String prev = "v" + getPreviousVersion();
        String current = "HEAD";
        LOG.lifecycle("  Generating release note for revisions: {} -> {}", prev, current);
        String v = this.getProject().getVersion().toString();
        String newContent = builder.buildNotes(v, prev, current, gitHubLabelMapping);
        return newContent;
    }

    /**
     * Generates incremental, detailed release notes text
     * and appends it to the top of the release notes file.
     */
    public static class UpdateTask extends IncrementalReleaseNotes {

        /**
         * Delegates to {@link IncrementalReleaseNotes#getReleaseNotesFile()}.
         * Configured here only to specify Gradle's output file and make the task incremental.
         */
        @OutputFile
        public File getReleaseNotesFile() {
            return super.getReleaseNotesFile();
        }

        @TaskAction public void updateReleaseNotes() {
            String newContent = super.getNewContent();
            FileUtil.appendToTop(newContent, getReleaseNotesFile());
            LOG.lifecycle("  Successfully updated release notes!");
        }
    }

    /**
     * Generates incremental, detailed release notes text
     * and appends it to the top of the release notes file.
     */
    public static class PreviewTask extends IncrementalReleaseNotes {
        @TaskAction public void updateReleaseNotes() {
            String newContent = super.getNewContent();
            LOG.lifecycle("----------------\n" + newContent + "----------------");
        }
    }
}
