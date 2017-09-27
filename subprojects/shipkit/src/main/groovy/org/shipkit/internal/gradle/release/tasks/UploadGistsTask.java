package org.shipkit.internal.gradle.release.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.gradle.configuration.ShipkitConfiguration;

/**
 * Uploads files as gists to GitHub API.
 * Uses {@link UploadGistsTask#getFilesToUpload()} to determine the files that need to be uploaded.
 * Uploads each of them individually.
 */
public class UploadGistsTask extends DefaultTask {

    @Input private FileCollection filesToUpload;
    @Input private String gitHubApiUrl;
    @Input private String gitHubWriteToken;

    @TaskAction
    public void uploadFilesToGist() {
        new UploadGists().uploadGists(this);
    }

    /**
     * Collection of files for which Gists will be created
     */
    public FileCollection getFilesToUpload() {
        return filesToUpload;
    }

    /**
     * See {@link #getFilesToUpload()}
     */
    public void setFilesToUpload(FileCollection filesToUpload) {
        this.filesToUpload = filesToUpload;
    }

    /**
     * See {@link ShipkitConfiguration.GitHub#getApiUrl()}
     */
    public String getGitHubApiUrl() {
        return gitHubApiUrl;
    }

    /**
     * See {@link ShipkitConfiguration.GitHub#getApiUrl()}
     */
    public void setGitHubApiUrl(String gitHubApiUrl) {
        this.gitHubApiUrl = gitHubApiUrl;
    }

    /**
     * See {@link ShipkitConfiguration.GitHub#getWriteAuthToken()}
     */
    public String getGitHubWriteToken() {
        return gitHubWriteToken;
    }

    /**
     * See {@link ShipkitConfiguration.GitHub#getWriteAuthToken()}
     */
    public void setGitHubWriteToken(String gitHubWriteToken) {
        this.gitHubWriteToken = gitHubWriteToken;
    }
}
