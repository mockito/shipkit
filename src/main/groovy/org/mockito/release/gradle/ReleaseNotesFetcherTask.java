package org.mockito.release.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.mockito.release.internal.gradle.util.ReleaseNotesSerializer;
import org.mockito.release.notes.generator.ReleaseNotesGenerator;
import org.mockito.release.notes.generator.ReleaseNotesGenerators;
import org.mockito.release.notes.model.ReleaseNotesData;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import static java.util.Arrays.asList;

/**
 * Fetches release notes data information from Git and GitHub and serializes it to {@link #outputFile}.
 */
public class ReleaseNotesFetcherTask extends DefaultTask {

    @Input private String previousVersion;
    @Input private String version = getProject().getVersion().toString();
    @Input private String gitHubReadOnlyAuthToken;
    @Input private String gitHubRepository;
    @Input private String tagPrefix = "v";
    @Input private boolean onlyPullRequests = false;
    @Input private File gitWorkDir = getProject().getRootDir();
    @Input private Collection<String> gitHubLabels = Collections.emptyList();
    @OutputFile private File outputFile;

    /**
     * See {@link ReleaseConfiguration.GitHub#getReadOnlyAuthToken()}
     */
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
     * See {@link ReleaseConfiguration.GitHub#getRepository()}
     */
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
     * Previous released version we generate the release notes from.
     * See {@link ReleaseConfiguration#getPreviousReleaseVersion()}
     */
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
     * The file release notes data will be saved to
     */
    public File getOutputFile() {
        return outputFile;
    }

    /**
     * See {@link #getOutputFile()}
     */
    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * Version we generate release notes data for
     */
    public String getVersion() {
        return version;
    }

    /**
     * See {@link #getVersion()}
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Whether to include only pull requests in the release notes data
     */
    public boolean isOnlyPullRequests() {
        return onlyPullRequests;
    }

    /**
     * See {@link #isOnlyPullRequests()}
     */
    public void setOnlyPullRequests(boolean onlyPullRequests) {
        this.onlyPullRequests = onlyPullRequests;
    }

    /**
     * See {@link ReleaseConfiguration.Git#getTagPrefix()}
     */
    public String getTagPrefix() {
        return tagPrefix;
    }

    /**
     * See {@link #getTagPrefix()}
     */
    public void setTagPrefix(String tagPrefix) {
        this.tagPrefix = tagPrefix;
    }

    /**
     * Work directory where git operations will be invoked (like 'git log', etc.)
     */
    public File getGitWorkDir() {
        return gitWorkDir;
    }

    /**
     * See {@link #getGitWorkDir()}
     */
    public void setGitWorkDir(File gitWorkDir) {
        this.gitWorkDir = gitWorkDir;
    }

    /**
     * GitHub labels to include when querying GitHub issues API.
     * If empty, then all labels will be included.
     * If labels are configured, only tickets with those labels will be included in the release notes.
     */
    public Collection<String> getGitHubLabels() {
        return gitHubLabels;
    }

    /**
     * See {@link #getGitHubLabels()}
     */
    public void setGitHubLabels(Collection<String> gitHubLabels) {
        this.gitHubLabels = gitHubLabels;
    }

    @TaskAction
    public void generateReleaseNotes() {
        ReleaseNotesGenerator generator = ReleaseNotesGenerators.releaseNotesGenerator(
                gitWorkDir, gitHubRepository, gitHubReadOnlyAuthToken);

        Collection<ReleaseNotesData> releaseNotes = generator.generateReleaseNotesData(
                version, asList(previousVersion), tagPrefix, gitHubLabels, onlyPullRequests);

        ReleaseNotesSerializer releaseNotesSerializer = new ReleaseNotesSerializer(getOutputFile());
        releaseNotesSerializer.serialize(releaseNotes);
    }
}
