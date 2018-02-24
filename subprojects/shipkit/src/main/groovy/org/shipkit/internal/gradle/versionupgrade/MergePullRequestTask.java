package org.shipkit.internal.gradle.versionupgrade;

import java.io.IOException;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

/**
 * Merge a pull request in {@link MergePullRequestTask#upstreamRepositoryName} if all checks succeed
 *
 * It is assumed that task is performed on fork repository, so {@link MergePullRequestTask#forkRepositoryName}
 * is based on origin repo, see {@link org.shipkit.internal.gradle.git.tasks.GitOriginRepoProvider}
 * and {@link MergePullRequestTask#upstreamRepositoryName} is based on {@link org.shipkit.gradle.configuration.ShipkitConfiguration.GitHub#getRepository()}
 */
public class MergePullRequestTask extends DefaultTask {

    @Input private String upstreamRepositoryName;
    @Input private String gitHubApiUrl;
    @Input private String authToken;
    @Input private String forkRepositoryName;
    @Input private String versionBranch;
    @Input private String sha;
    @Input private String url;

    private String baseBranch;
    private boolean dryRun;
    private int pullrequestNumber;

    @TaskAction
    public void mergePullRequest() throws IOException {
        new MergePullRequest().mergePullRequest(this);
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

    /**
     * It is assumed that this task is performed on fork of the upstream repo, so this value is taken from
     * git remote origin. See {@link org.shipkit.internal.gradle.git.tasks.GitOriginRepoProvider}
     */
    public String getForkRepositoryName() {
        return forkRepositoryName;
    }

    /**
     * See {@link #getForkRepositoryName()}
     */
    public void setForkRepositoryName(String forkRepositoryName) {
        this.forkRepositoryName = forkRepositoryName;
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
    public String getAuthToken() {
        return authToken;
    }

    /**
     * See {@link org.shipkit.gradle.configuration.ShipkitConfiguration.GitHub#getWriteAuthToken()}
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    /**
     * See {@link org.shipkit.gradle.configuration.ShipkitConfiguration.GitHub#dryRun}
     */
    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    /**
     * See {@link org.shipkit.gradle.configuration.ShipkitConfiguration.GitHub#dryRun}
     */
    public boolean isDryRun() {
        return dryRun;
    }

    /**
     * Head branch of pull request
     */
    public String getVersionBranch() {
        return versionBranch;
    }

    /**
     * See {@link #getVersionBranch()}
     */
    public void setVersionBranch(String versionBranch) {
        this.versionBranch = versionBranch;
    }

    public void setPullRequestNumber(int pullRequestNumber) {
        this.pullrequestNumber = pullRequestNumber;
    }

    public int getPullrequestNumber() {
        return pullrequestNumber;
    }

    /**
     * Sha of pull request
     */
    public String getPullRequestSha() {
        return sha;
    }

    /**
     * See {@link #getPullRequestSha()}
     */
    public void setPullRequestSha(String sha) {
        this.sha = sha;
    }

    /**
     * Url of pull request
     */
    public String getPullRequestUrl() {
        return url;
    }

    /**
     * See {@link #getPullRequestUrl()}
     */
    public void setPullRequestUrl(String url) {
        this.url = url;
    }

    /**
     * Original branch we want merge PullRequest to
     */
    public String getBaseBranch() {
        return baseBranch;
    }

    /**
     * See {@link #getBaseBranch()}
     */
    public void setBaseBranch(String baseBranch) {
        this.baseBranch = baseBranch;
    }
}
