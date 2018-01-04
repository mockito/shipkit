package org.shipkit.internal.gradle.versionupgrade;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.json.simple.DeserializationException;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.shipkit.internal.gradle.git.OpenPullRequest;
import org.shipkit.internal.util.GitHubApi;

import java.io.IOException;

class FindOpenPullRequest {

    private static final Logger LOG = Logging.getLogger(FindOpenPullRequest.class);

    public OpenPullRequest findOpenPullRequest(FindOpenPullRequestTask task) throws IOException, DeserializationException {
        return findOpenPullRequest(task.getUpstreamRepositoryName(), task.getVersionBranchRegex(),
            new GitHubApi(task.getGitHubApiUrl(), task.getAuthToken()));
    }

    public OpenPullRequest findOpenPullRequest(String upstreamRepositoryName, String versionBranchRegex, GitHubApi gitHubApi) throws IOException, DeserializationException {
        String response = gitHubApi.get("/repos/" + upstreamRepositoryName + "/pulls?state=open");

        JsonArray pullRequests = Jsoner.deserialize(response, new JsonArray());

        for (Object pullRequest : pullRequests) {
            JsonObject head = (JsonObject) ((JsonObject) pullRequest).get("head");
            String branchName = head.getString("ref");
            if (branchName.matches(versionBranchRegex)) {
                LOG.lifecycle("  Found an open pull request with version upgrade on branch {}", branchName);
                OpenPullRequest openPullRequest = new OpenPullRequest();
                openPullRequest.setRef(head.getString("ref"));
                openPullRequest.setSha(head.getString("sha"));
                return openPullRequest;
            }
        }

        LOG.lifecycle("  New pull request will be opened because we didn't find an existing PR to reuse.");

        return null;
    }

}
