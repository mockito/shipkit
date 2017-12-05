package org.shipkit.internal.gradle.versionupgrade;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskAction;
import org.json.simple.DeserializationException;
import org.shipkit.gradle.configuration.ShipkitConfiguration;

import java.io.IOException;

/**
 * Looks for an open pull request with a version upgrade by:
 * - querying GitHubAPI of {@link #upstreamRepositoryName} for all open pull requests
 * - checks if any HEAD branch of resulting pull requests matches {@link #versionBranchRegex}
 */
public class FindOpenPullRequestTask extends DefaultTask {

    private String upstreamRepositoryName;
    private String gitHubApiUrl;
    private String authToken;
    private String versionBranchRegex;

    private String openPullRequestBranch;

    @TaskAction
    public void findOpenPullRequest() throws IOException, DeserializationException {
        openPullRequestBranch = new FindOpenPullRequest().findOpenPullRequest(this);
    }

    /**
     * See {@link ShipkitConfiguration.GitHub#getReadOnlyAuthToken()}
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * See {@link ShipkitConfiguration.GitHub#getReadOnlyAuthToken()}
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
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
     * See {@link ShipkitConfiguration.GitHub#getRepository()}
     */
    public String getUpstreamRepositoryName() {
        return upstreamRepositoryName;
    }

    /**
     * See {@link ShipkitConfiguration.GitHub#getRepository()}
     */
    public void setUpstreamRepositoryName(String upstreamRepositoryName) {
        this.upstreamRepositoryName = upstreamRepositoryName;
    }

    /**
     * Regex matching version upgrade branch for any version.
     * It's a combination of:
     * - {@link UpgradeDependencyPlugin#getVersionBranchName}
     * - {@link ReplaceVersionTask#VERSION_REGEX}
     */
    public String getVersionBranchRegex() {
        return versionBranchRegex;
    }

    /**
     * See {@link #getVersionBranchRegex()}
     */
    public void setVersionBranchRegex(String versionBranchRegex) {
        this.versionBranchRegex = versionBranchRegex;
    }

    /**
     * Returns branch of the current open pull request with version upgrade or null if it doesn't exist.
     */
    public String getOpenPullRequestBranch() {
        return openPullRequestBranch;
    }

    /**
     * Call if you want {@param #openPullRequestBranchCallback} to be executed after this task is finished.
     */
    public void provideOpenPullRequestBranch(Task t, final Action<String> openPullRequestBranchCallback) {
        t.dependsOn(this);
        this.doLast(new Action<Task>() {
            public void execute(Task task) {
                openPullRequestBranchCallback.execute(openPullRequestBranch);
            }
        });
    }
}
