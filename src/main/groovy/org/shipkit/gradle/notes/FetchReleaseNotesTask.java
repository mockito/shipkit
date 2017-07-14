package org.shipkit.gradle.notes;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.gradle.ReleaseConfiguration;
import org.shipkit.internal.gradle.notes.tasks.FetchReleaseNotes;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

/**
 * Fetches release notes data information from Git and GitHub
 * and serializes it to file specified with {@link #outputFile}.
 */
public class FetchReleaseNotesTask extends DefaultTask {

    @Input @Optional private String previousVersion;
    @Input private String version = getProject().getVersion().toString();
    @Input private String gitHubApiUrl;
    @Input private String gitHubReadOnlyAuthToken;
    @Input private String gitHubRepository;
    @Input private String tagPrefix = "v";
    @Input private boolean onlyPullRequests;
    @Input private File gitWorkDir = getProject().getRootDir();
    @Input private Collection<String> gitHubLabels = Collections.emptyList();
    @Input private Collection<String> ignoreCommitsContaining;
    @OutputFile private File outputFile;

    @TaskAction
    public void fetchReleaseNotes() {
        new FetchReleaseNotes().fetchReleaseNotes(this);
    }

    /**
     * See {@link ReleaseConfiguration.GitHub#getUrl()}
     */
    public String getGitHubApiUrl() {
        return gitHubApiUrl;
    }

    /**
     * See {@link #getGitHubApiUrl()}
     */
    public void setGitHubApiUrl(String gitHubApiUrl) {
        this.gitHubApiUrl = gitHubApiUrl;
    }

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

    /**
     * See {@link ReleaseConfiguration.ReleaseNotes#getIgnoreCommitsContaining()}
     */
    public Collection<String> getIgnoreCommitsContaining() {
        return ignoreCommitsContaining;
    }

    /**
     * See {@link #getIgnoreCommitsContaining()}
     */
    public void setIgnoreCommitsContaining(Collection<String> ignoreCommitsContaining) {
        this.ignoreCommitsContaining = ignoreCommitsContaining;
    }
}
