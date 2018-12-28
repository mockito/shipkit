package org.shipkit.gradle.notes;

import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.gradle.notes.tasks.UpdateReleaseNotesOnGitHub;
import org.shipkit.internal.notes.header.HeaderProvider;

/**
 * Generates incremental, detailed release notes text and appends them to the file {@link #getReleaseNotesFile()}.
 * When preview mode is enabled ({@link #isPreviewMode()}), the new release notes content is displayed only (file is not updated).
 */
public class UpdateReleaseNotesOnGitHubTask extends AbstractReleaseNotesTask {

    @Input private String gitHubApiUrl;
    @Input private String gitHubWriteToken;
    @Input private String upstreamRepositoryName;

    /**
     * Generates incremental release notes and appends it to the top of release notes file.
     */
    @TaskAction
    public void updateReleaseNotesOnGitHub() throws Exception {
        new UpdateReleaseNotesOnGitHub().updateReleaseNotes(this, new HeaderProvider());
    }

    /**
     * See {@link org.shipkit.gradle.configuration.ShipkitConfiguration.GitHub#getApiUrl()}
     */
    public String getGitHubApiUrl() {
        return gitHubApiUrl;
    }

    /**
     * See {@link org.shipkit.gradle.configuration.ShipkitConfiguration.GitHub#getApiUrl()}
     */
    public void setGitHubApiUrl(String gitHubApiUrl) {
        this.gitHubApiUrl = gitHubApiUrl;
    }

    /**
     * See {@link org.shipkit.gradle.configuration.ShipkitConfiguration.GitHub#getWriteAuthToken()}
     */
    public String getGitHubWriteToken() {
        return gitHubWriteToken;
    }

    /**
     * See {@link org.shipkit.gradle.configuration.ShipkitConfiguration.GitHub#getWriteAuthToken()}
     */
    public void setGitHubWriteToken(String gitHubWriteToken) {
        this.gitHubWriteToken = gitHubWriteToken;
    }

    /**
     * See {@link org.shipkit.gradle.configuration.ShipkitConfiguration.GitHub#getRepository()}
     */
    public String getUpstreamRepositoryName() {
        return upstreamRepositoryName;
    }

    /**
     * See {@link org.shipkit.gradle.configuration.ShipkitConfiguration.GitHub#getRepository()}
     */
    public void setUpstreamRepositoryName(String upstreamRepositoryName) {
        this.upstreamRepositoryName = upstreamRepositoryName;
    }
}
