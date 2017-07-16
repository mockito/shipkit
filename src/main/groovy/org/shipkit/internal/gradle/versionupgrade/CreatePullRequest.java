package org.shipkit.internal.gradle.versionupgrade;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.internal.util.GitHubApi;

import java.io.IOException;

class CreatePullRequest {

    private static final Logger LOG = Logging.getLogger(CreatePullRequest.class);

    public void createPullRequest(CreatePullRequestTask task) throws IOException {
        createPullRequest(task, new GitHubApi(task.getGitHubApiUrl(), task.getAuthToken()));
    }

    public void createPullRequest(CreatePullRequestTask task, GitHubApi gitHubApi) throws IOException {
        if(task.isDryRun()){
            LOG.lifecycle("  Skipping pull request creation due to dryRun = true");
            return;
        }

        LOG.lifecycle("  Creating a pull request of title '{}' in repository '{}' between base = '{}' and head = '{}'.",
            task.getTitle(), task.getRepositoryUrl(), task.getBaseBranch(), task.getHeadBranch());

        String body = "{" +
            "  \"title\": \"" + task.getTitle() + "\"," +
            "  \"body\": \"Please pull this in!\"," +
            "  \"head\": \"" + task.getHeadBranch() + "\"," +
            "  \"base\": \"" + task.getBaseBranch() + "\"" +
            "}";

        gitHubApi.post("/repos/" + task.getRepositoryUrl() + "/pulls", body);
    }
}
