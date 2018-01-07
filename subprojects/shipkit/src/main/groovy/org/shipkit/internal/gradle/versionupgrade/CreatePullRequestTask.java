package org.shipkit.internal.gradle.versionupgrade;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.internal.gradle.git.OpenPullRequest;

import java.io.IOException;

/**
 * Creates a pull request in {@link CreatePullRequestTask#upstreamRepositoryName} between
 * {@link UpgradeDependencyExtension#baseBranch} and {@link CreatePullRequestTask#versionBranch} from
 * {@link CreatePullRequestTask#forkRepositoryName}
 *
 * It is assumed that task is performed on fork repository, so {@link CreatePullRequestTask#forkRepositoryName}
 * is based on origin repo, see {@link org.shipkit.internal.gradle.git.tasks.GitOriginRepoProvider}
 * and {@link CreatePullRequestTask#upstreamRepositoryName} is based on {@link org.shipkit.gradle.configuration.ShipkitConfiguration.GitHub#getRepository()}
 */
public class CreatePullRequestTask extends DefaultTask {

    @Input private String upstreamRepositoryName;
    @Input private String gitHubApiUrl;
    @Input private String authToken;
    @Input private String versionBranch;
    @Input private String forkRepositoryName;
    @Input private String pullRequestDescription;
    @Input private String pullRequestTitle;

    private boolean dryRun;
    private OpenPullRequest pullRequest;
    private String baseBranch;

    @TaskAction
    public void createPullRequest() throws IOException {
        pullRequest = new CreatePullRequest().createPullRequest(this);
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
     * Description of pull request.
     */
    public String getPullRequestDescription() {
        return pullRequestDescription;
    }

    /**
     * See {@link #getPullRequestDescription()}
     */
    public void setPullRequestDescription(String pullRequestDescription) {
        this.pullRequestDescription = pullRequestDescription;
    }

    /**
     * Title of pull request.
     */
    public String getPullRequestTitle() {
        return pullRequestTitle;
    }

    /**
     * See {@link #getPullRequestTitle()}
     */
    public void setPullRequestTitle(String pullRequestTitle) {
        this.pullRequestTitle = pullRequestTitle;
    }

    /**
     * Data of created PullRequest
     */
    public OpenPullRequest getPullRequest() {
        return pullRequest;
    }

    /**
     * See {@link #getPullRequest()}
     */
    public void setPullRequest(OpenPullRequest pullRequest) {
        this.pullRequest = pullRequest;
    }

    /**
     * Call if you want {@param #branchAction} to be executed after this task is finished.
     *
     * Sometimes a task may need information about open pull request branch but the problem lies in figuring out
     * the correct time when the task should call {@link #getPullRequest()}, because this value is only available
     * after {@link CreatePullRequestTask} is executed.
     *
     * Using this method guarantees that:
     * - the value will be already available when the callback {@param #branchAction} is executed
     * - the task {@param #dependant} is executed after {@link CreatePullRequestTask}
     */

    public void provideCreatedPullRequest(Task dependant, final Action<OpenPullRequest> branchAction) {
        dependant.dependsOn(this);
        this.doLast(new Action<Task>() {
            public void execute(Task task) {
                branchAction.execute(pullRequest);
            }
        });
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
