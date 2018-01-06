package org.shipkit.internal.gradle.versionupgrade;

import java.io.IOException;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.shipkit.internal.gradle.git.OpenPullRequest;
import org.shipkit.internal.gradle.util.BranchUtils;
import org.shipkit.internal.util.GitHubApi;
import org.shipkit.internal.util.IncubatingWarning;

class CreatePullRequest {

    private static final Logger LOG = Logging.getLogger(CreatePullRequest.class);

    public OpenPullRequest createPullRequest(CreatePullRequestTask task) throws IOException {
        return createPullRequest(task, new GitHubApi(task.getGitHubApiUrl(), task.getAuthToken()));
    }

    public OpenPullRequest createPullRequest(CreatePullRequestTask task, GitHubApi gitHubApi) throws IOException {
        if (task.isDryRun()) {
            LOG.lifecycle("  Skipping pull request creation due to dryRun = true");
            return null;
        }

        String headBranch = BranchUtils.getHeadBranch(task.getForkRepositoryName(), task.getVersionBranch());

        IncubatingWarning.warn("creating pull requests");
        LOG.lifecycle("  Creating a pull request of title '{}' in repository '{}' between base = '{}' and head = '{}'.",
            task.getPullRequestTitle(), task.getUpstreamRepositoryName(), task.getVersionUpgrade().getBaseBranch(), headBranch);

        String body = "{" +
            "  \"title\": \"" + task.getPullRequestTitle() + "\"," +
            "  \"body\": \"" + task.getPullRequestDescription() + "\"," +
            "  \"head\": \"" + headBranch + "\"," +
            "  \"base\": \"" + task.getVersionUpgrade().getBaseBranch() + "\"," +
            "  \"maintainer_can_modify\": true" +
            "}";

        String response = gitHubApi.post("/repos/" + task.getUpstreamRepositoryName() + "/pulls", body);
        JsonObject pullRequest = Jsoner.deserialize(response, new JsonObject());
        return BranchUtils.getOpenPullRequest(pullRequest, null);
    }
}
