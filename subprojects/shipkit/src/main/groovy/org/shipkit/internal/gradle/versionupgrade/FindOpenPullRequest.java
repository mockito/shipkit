package org.shipkit.internal.gradle.versionupgrade;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.json.simple.DeserializationException;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.shipkit.internal.gradle.git.domain.PullRequest;
import org.shipkit.internal.gradle.util.BranchUtils;
import org.shipkit.internal.util.GitHubApi;

import java.io.IOException;

class FindOpenPullRequest {

    private static final Logger LOG = Logging.getLogger(FindOpenPullRequest.class);

    public PullRequest findOpenPullRequest(FindOpenPullRequestTask task) throws IOException, DeserializationException {
        return findOpenPullRequest(task.getUpstreamRepositoryName(), task.getVersionBranchRegex(),
            new GitHubApi(task.getGitHubApiUrl(), task.getAuthToken()));
    }

    public PullRequest findOpenPullRequest(String upstreamRepositoryName, String versionBranchRegex, GitHubApi gitHubApi) throws IOException, DeserializationException {
        String response = gitHubApi.get("/repos/" + upstreamRepositoryName + "/pulls?state=open");

        JsonArray pullRequests = Jsoner.deserialize(response, new JsonArray());

        for (Object pullRequest : pullRequests) {
            PullRequest openPullRequest = BranchUtils.getOpenPullRequest((JsonObject) pullRequest, versionBranchRegex);
            if (openPullRequest != null) {
                return openPullRequest;
            }
        }

        LOG.lifecycle("  New pull request will be opened because we didn't find an existing PR to reuse.");

        return null;
    }
}
