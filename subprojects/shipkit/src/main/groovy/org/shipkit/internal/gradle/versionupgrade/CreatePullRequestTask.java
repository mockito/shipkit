package org.shipkit.internal.gradle.versionupgrade;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.gradle.configuration.ShipkitConfiguration;

import java.io.IOException;

/**
 * Creates a pull request in {@link CreatePullRequestTask#upstreamRepositoryName} between
 * {@link UpgradeDependencyExtension#baseBranch} and {@link CreatePullRequestTask#versionBranch} from
 * {@link CreatePullRequestTask#forkRepositoryName}
 *
 * It is assumed that task is performed on fork repository, so {@link CreatePullRequestTask#forkRepositoryName}
 * is based on origin repo, see {@link org.shipkit.internal.gradle.git.tasks.GitOriginRepoProvider}
 * and {@link CreatePullRequestTask#upstreamRepositoryName} is based on {@link ShipkitConfiguration.GitHub#getRepository()}
 */
public class CreatePullRequestTask extends DefaultTask {

    @Input private String upstreamRepositoryName;
    @Input private String gitHubApiUrl;
    @Input private String authToken;
    @Input private String versionBranch;
    @Input private String forkRepositoryName;

    private boolean dryRun;
    private UpgradeDependencyExtension versionUpgrade;

    @TaskAction
    public void createPullRequest() throws IOException {
        new CreatePullRequest().createPullRequest(this);
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
    public String getAuthToken() {
        return authToken;
    }

    /**
     * See {@link ShipkitConfiguration.GitHub#getWriteAuthToken()}
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
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

    public UpgradeDependencyExtension getVersionUpgrade() {
        return versionUpgrade;
    }

    public void setVersionUpgrade(UpgradeDependencyExtension versionUpgrade) {
        this.versionUpgrade = versionUpgrade;
    }

    /**
     * See {@link ShipkitConfiguration.GitHub#dryRun}
     */
    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }

    /**
     * See {@link ShipkitConfiguration.GitHub#dryRun}
     */
    public boolean isDryRun() {
        return dryRun;
    }
}
