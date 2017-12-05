package org.shipkit.internal.gradle.versionupgrade;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.json.simple.DeserializationException;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.shipkit.internal.util.GitHubApi;

import java.io.IOException;

class FindOpenPullRequest {

    private static final Logger LOG = Logging.getLogger(FindOpenPullRequest.class);

    public String findOpenPullRequest(FindOpenPullRequestTask task) throws IOException, DeserializationException {
        return findOpenPullRequest(task, new GitHubApi(task.getGitHubApiUrl(), task.getAuthToken()));
    }

    public String findOpenPullRequest(FindOpenPullRequestTask task, GitHubApi gitHubApi) throws IOException, DeserializationException {
        String response = gitHubApi.get("/repos/" + task.getUpstreamRepositoryName() + "/pulls?state=open");

        JsonArray pullRequests = Jsoner.deserialize(response, new JsonArray());

        for (Object pullRequest : pullRequests) {
            JsonObject head = (JsonObject) ((JsonObject) pullRequest).get("head");
            String branchName = head.getString("ref");
            if (branchName.matches(task.getVersionBranchRegex())) {
                LOG.lifecycle("  Found an open pull request with version upgrade on branch {}", branchName);
                return head.getString("ref");
            }
        }

        LOG.lifecycle("  No open pull request with version upgrade found.");

        return null;
    }

}
