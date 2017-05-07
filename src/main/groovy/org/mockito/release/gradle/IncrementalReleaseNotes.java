package org.mockito.release.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.*;
import org.mockito.release.internal.gradle.util.FileUtil;
import org.mockito.release.internal.gradle.util.ReleaseNotesSerializer;
import org.mockito.release.notes.contributors.ContributorsSerializer;
import org.mockito.release.notes.contributors.ContributorsSet;
import org.mockito.release.notes.format.ReleaseNotesFormatters;
import org.mockito.release.notes.model.Contribution;
import org.mockito.release.notes.model.ReleaseNotesData;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generates incremental, detailed release notes text.
 * that can be appended to the release notes file.
 */
public abstract class IncrementalReleaseNotes extends DefaultTask {

    private static final Logger LOG = Logging.getLogger(IncrementalReleaseNotes.class);

    private String previousVersion;
    private File releaseNotesFile;
    private String gitHubRepository;
    private Map<String, String> gitHubLabelMapping = new LinkedHashMap<String, String>();
    private String publicationRepository;
    private File releaseNotesData;
    private File contributorsData;

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

    /**
     * The target repository where the publications / binaries are published to.
     * Shown in the release notes.
     */
    @Input
    public String getPublicationRepository() {
        return publicationRepository;
    }

    /**
     * See {@link #getPublicationRepository()}
     */
    public void setPublicationRepository(String publicationRepository) {
        this.publicationRepository = publicationRepository;
    }

    /**
     * Previous released version we generate the release notes from.
     */
    @Input
    public String getPreviousVersion() {
        return previousVersion;
    }

    /**
     * See {@link #getPreviousVersion()}
     */
    public void setPreviousVersion(String previousVersion) {
        this.previousVersion = previousVersion;
    }

    /**
     * Input to the release notes generation,
     * serialized release notes data objects of type {@link ReleaseNotesData}.
     * They are used to generate formatted release notes.
     */
    @InputFile
    public File getReleaseNotesData() {
        return releaseNotesData;
    }

    /**
     * See {@link #getReleaseNotesData()}
     */
    public void setReleaseNotesData(File releaseNotesData) {
        this.releaseNotesData = releaseNotesData;
    }

    /**
     * Serialized contributors data to be included in release notes
     */
    public File getContributorsData() {
        return contributorsData;
    }

    /**
     * See {@link #getContributorsData()}
     */
    public void setContributorsData(File contributorsData) {
        this.contributorsData = contributorsData;
    }

    private void assertConfigured() {
        //TODO SF unit test coverage
        if (releaseNotesFile == null || !releaseNotesFile.isFile()) {
            throw new GradleException("'" + this.getPath() + ".releaseNotesFile' must be configured and the file must be present.");
        }

        if (gitHubRepository == null || gitHubRepository.trim().isEmpty()) {
            throw new GradleException("'" + this.getPath() + "gitHubRepository' must be configured.");
        }
    }

    /**
     * Generates new incremental content of the release notes.
     */
    protected String getNewContent() {
        assertConfigured();
        LOG.lifecycle("  Building new release notes based on {}", releaseNotesFile);

        String version = getProject().getVersion().toString();
        String tagPrefix = "v";

        Collection<ReleaseNotesData> data = new ReleaseNotesSerializer(releaseNotesData).deserialize();
        ContributorsSet contributors = new ContributorsSerializer(contributorsData).deserialize();
        //TODO this is not nice at all. Suggested plan:
        // Merge the functionality of recent contributors fetching + all contributors fetching.
        // 1. Provide a single service that will:
        //   - fetch all contributors using https://developer.github.com/v3/repos/#list-contributors
        //   - then fetch recent contributors (last 48hrs) using https://developer.github.com/v3/repos/commits/#list-commits-on-a-repository
        // 2. Have just a single Gradle task that gets contributors data instead of current 2 (recent + all)
        // 3. Release notes fetcher task will depend on contributors so
        // that it will product complete release notes data
        for (ReleaseNotesData d : data) {
            for (Contribution c : d.getContributions().getContributions()) {
                c.setContributor(contributors.findByAuthorName(c.getAuthorName()));
            }
        }

        String vcsCommitTemplate = "https://github.com/" + gitHubRepository + "/compare/"
                + tagPrefix + previousVersion + "..." + tagPrefix + version;
        String notes = ReleaseNotesFormatters.detailedFormatter(
                "", gitHubLabelMapping, vcsCommitTemplate, publicationRepository)
                .formatReleaseNotes(data);

        return notes + "\n\n";
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
