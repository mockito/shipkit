package org.shipkit.internal.gradle.versionupgrade;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.gradle.configuration.ShipkitConfiguration;

import java.io.IOException;

/**
 * Creates a pull request in {@link CreatePullRequestTask#repositoryName} between
 * {@link VersionUpgradeConsumerExtension#baseBranch} and {@link CreatePullRequestTask#headBranch}
 */
public class CreatePullRequestTask extends DefaultTask{

    private String repositoryName;
    private String gitHubApiUrl;
    private String authToken;
    private String headBranch;
    private boolean dryRun;
    private VersionUpgradeConsumerExtension versionUpgrade;

    @TaskAction
    public void createPullRequest() throws IOException {
        new CreatePullRequest().createPullRequest(this);
    }

    /**
     * See {@link ShipkitConfiguration.GitHub#getRepository()}
     */
    public String getRepositoryName() {
        return repositoryName;
    }

    /**
     * See {@link ShipkitConfiguration.GitHub#getRepository()}
     */
    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
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
    public String getHeadBranch() {
        return headBranch;
    }

    /**
     * See {@link #getHeadBranch()}
     */
    public void setHeadBranch(String headBranch) {
        this.headBranch = headBranch;
    }

    public VersionUpgradeConsumerExtension getVersionUpgrade() {
        return versionUpgrade;
    }

    public void setVersionUpgrade(VersionUpgradeConsumerExtension versionUpgrade) {
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
