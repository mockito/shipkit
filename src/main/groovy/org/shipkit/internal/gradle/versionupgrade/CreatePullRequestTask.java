package org.shipkit.internal.gradle.versionupgrade;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;
import org.shipkit.gradle.ReleaseConfiguration;
import org.shipkit.internal.util.ExposedForTesting;
import org.shipkit.internal.util.GitHubApi;

import java.io.IOException;

/**
 * Creates a pull request in {@link CreatePullRequestTask#repositoryUrl} of title {@link CreatePullRequestTask#title}
 * between {@link CreatePullRequestTask#baseBranch} and {@link CreatePullRequestTask#headBranch}
 */
public class CreatePullRequestTask extends DefaultTask{

    private static final Logger LOG = Logging.getLogger(CreatePullRequestTask.class);

    private String repositoryUrl;
    private String gitHubApiUrl;
    private String authToken;
    private String title;
    private String headBranch;
    private String baseBranch;

    private GitHubApi gitHubApi;

    @TaskAction
    public void createPullRequest() throws IOException {
        if(gitHubApi == null) {
            gitHubApi = new GitHubApi(gitHubApiUrl, authToken);
        }

        LOG.lifecycle("  Creating a pull request of title '{}' in repository '{}' between base = '{}' and head = '{}'.",
            title, repositoryUrl, baseBranch, headBranch);

        String body = "{" +
            "  \"title\": \"" + title + "\"," +
            "  \"body\": \"Please pull this in!\"," +
            "  \"head\": \"" + headBranch + "\"," +
            "  \"base\": \"" + baseBranch + "\"" +
            "}";

        try {
            gitHubApi.post("/repos/" + repositoryUrl + "/pulls", body);
        } catch(IOException e){
            LOG.error("  Creating a pull request failed.\n  {}", e.getMessage());
        }
    }


    /**
     * See {@link ReleaseConfiguration.GitHub#getRepository()}
     */
    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    /**
     * See {@link ReleaseConfiguration.GitHub#getRepository()}
     */
    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    /**
     * See {@link ReleaseConfiguration.GitHub#getApiUrl()}
     */
    public String getGitHubApiUrl() {
        return gitHubApiUrl;
    }

    /**
     * See {@link ReleaseConfiguration.GitHub#getApiUrl()}
     */
    public void setGitHubApiUrl(String gitHubApiUrl) {
        this.gitHubApiUrl = gitHubApiUrl;
    }

    /**
     * See {@link ReleaseConfiguration.GitHub#getWriteAuthToken()}
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * See {@link ReleaseConfiguration.GitHub#getWriteAuthToken()}
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    /**
     * Title of pull request
     */
    public String getTitle() {
        return title;
    }

    /**
     * See {@link #getTitle()}
     */
    public void setTitle(String title) {
        this.title = title;
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

    /**
     * Base branch of pull request
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

    @ExposedForTesting
    protected void setGitHubApi(GitHubApi gitHubApi){
        this.gitHubApi = gitHubApi;
    }
}
