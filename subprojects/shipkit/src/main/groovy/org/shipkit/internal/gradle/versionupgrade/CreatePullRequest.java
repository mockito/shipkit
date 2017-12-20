package org.shipkit.internal.gradle.versionupgrade;

import java.io.IOException;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.internal.gradle.util.BranchUtils;
import org.shipkit.internal.util.GitHubApi;
import org.shipkit.internal.util.IncubatingWarning;

class CreatePullRequest {

    private static final Logger LOG = Logging.getLogger(CreatePullRequest.class);

    public void createPullRequest(CreatePullRequestTask task) throws IOException {
        createPullRequest(task, new GitHubApi(task.getGitHubApiUrl(), task.getAuthToken()));
    }

    public void createPullRequest(CreatePullRequestTask task, GitHubApi gitHubApi) throws IOException {
        if (task.isDryRun()) {
            LOG.lifecycle("  Skipping pull request creation due to dryRun = true");
            return;
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

        gitHubApi.post("/repos/" + task.getUpstreamRepositoryName() + "/pulls", body);
    }
}
