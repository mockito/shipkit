package org.shipkit.internal.gradle.release.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.gradle.configuration.ShipkitConfiguration;

import java.util.List;

/**
 * Uploads files as gists to GitHub API.
 * Uses {@link UploadGistsTask#getFilesPatterns()} to determine the files that need to be uploaded.
 * Uploads each of them individually.
 * Looks for files only in the {@link UploadGistsTask#getRootDir()}.
 */
public class UploadGistsTask extends DefaultTask {

    private String rootDir;
    @Input private List<String> filesPatterns;
    @Input private String gitHubApiUrl;
    @Input private String gitHubWriteToken;

    @TaskAction
    public void uploadFilesToGist() {
        new UploadGists().uploadGists(this);
    }

    /**
     * List of patterns such that all of the files that match them would be uploaded to Gist.
     * All of the patterns are in Ant format, @see <a href="https://ant.apache.org/manual/dirtasks.html#patterns">https://ant.apache.org/manual/dirtasks.html#patterns</a>
     * eg. ["**.log", "**\/**.txt"]
     * BEWARE! Only files that are below {@link #getRootDir()} would be included in the results.
     */
    public List<String> getFilesPatterns() {
        return filesPatterns;
    }

    /**
     * See {@link #getFilesPatterns()}
     */
    public void setFilesPatterns(List<String> filesPatterns) {
        this.filesPatterns = filesPatterns;
    }


    /**
     * Directory where search for files matching {@link #getFilesPatterns()} would be initiated.
     */
    public String getRootDir() {
        return rootDir;
    }

    /**
     * See {@link #getRootDir()}
     */
    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
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
