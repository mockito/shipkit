package org.shipkit.internal.gradle.versionupgrade;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskAction;
import org.json.simple.DeserializationException;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.internal.gradle.git.OpenPullRequest;

import java.io.IOException;

/**
 * Looks for an open pull request with a version upgrade by:
 * - querying GitHubAPI of {@link #upstreamRepositoryName} for all open pull requests
 * - checking if any HEAD branch of resulting pull requests matches {@link #versionBranchRegex}
 *
 * {@link #versionBranchRegex} can be used because all branches created by Shipkit for version upgrade purposes
 * are named the same way. Eg. "upgrade-mockito-to-1.2.4". The only thing that changes between branches for
 * specific versions is the version number so it's easy to use a regular expression to find all of them.
 * For details see {@link UpgradeDependencyPlugin#getVersionBranchName(String, String)}.
 */
public class FindOpenPullRequestTask extends DefaultTask {

    private String upstreamRepositoryName;
    private String gitHubApiUrl;
    private String authToken;
    private String versionBranchRegex;
    private OpenPullRequest openPullRequest;

    @TaskAction
    public void findOpenPullRequest() throws IOException, DeserializationException {
        openPullRequest = new FindOpenPullRequest().findOpenPullRequest(this);
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
     *
     * Eg. "upgrade-mockito-to-[0-9.]+".
     * All branches created by Shipkit for version upgrade purposes are named the same way.
     * The only thing that changes between branches for specific versions is the version number
     * so it's easy to use a regular expression to find all of them.
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
        return openPullRequest.getRef();
    }

    /**
     * Call if you want {@param #branchAction} to be executed after this task is finished.
     *
     * Sometimes a task may need information about open pull request branch but the problem lies in figuring out
     * the correct time when the task should call {@link #getOpenPullRequestBranch()}, because this value is only available
     * after {@link FindOpenPullRequestTask} is executed.
     *
     * Using this method guarantees that:
     * - the value will be already available when the callback {@param #branchAction} is executed
     * - the task {@param #dependant} is executed after {@link FindOpenPullRequestTask}
     */
    public void provideBranchTo(Task dependant, final Action<String> branchAction) {
        dependant.dependsOn(this);
        this.doLast(new Action<Task>() {
            public void execute(Task task) {
                branchAction.execute(openPullRequest.getRef());
            }
        });
    }

    public void provideOpenPullRequest(Task dependant, final Action<OpenPullRequest> branchAction) {
        dependant.dependsOn(this);
        this.doLast(new Action<Task>() {
            public void execute(Task task) {
                branchAction.execute(openPullRequest);
            }
        });
    }
}
