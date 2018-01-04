package org.shipkit.internal.gradle.versionupgrade;

import java.io.IOException;

import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.shipkit.internal.gradle.util.BranchUtils;
import org.shipkit.internal.util.GitHubApi;
import org.shipkit.internal.util.GitHubStatusCheck;
import org.shipkit.internal.util.IncubatingWarning;

class MergePullRequest {

    private static final Logger LOG = Logging.getLogger(MergePullRequest.class);

    public void mergePullRequest(MergePullRequestTask task) throws IOException {
        GitHubApi gitHubApi = new GitHubApi(task.getGitHubApiUrl(), task.getAuthToken());
        mergePullRequest(task, gitHubApi, new GitHubStatusCheck(task, gitHubApi));
    }

    public void mergePullRequest(MergePullRequestTask task, GitHubApi gitHubApi, GitHubStatusCheck gitHubStatusCheck) throws IOException {
        if (task.isDryRun()) {
            LOG.lifecycle(" Skipping pull request merging due to dryRun = true");
            return;
        }

        String headBranch = BranchUtils.getHeadBranch(task.getForkRepositoryName(), task.getVersionBranch());

        IncubatingWarning.warn("merge pull requests");
        LOG.lifecycle("Waiting for status of a pull request in repository '{}' between base = '{}' and head = '{}'.", task.getUpstreamRepositoryName(), task.getVersionUpgrade().getBaseBranch(), headBranch);

        String sha = task.getPullRequestSha();
        String url = task.getPullRequestUrl();
        String body = "{" +
            "  \"head\": \"" + headBranch + "\"," +
            "  \"base\": \"" + task.getVersionUpgrade().getBaseBranch() + "\"" +
            "}";

        try {
            boolean allChecksOk = gitHubStatusCheck.checkStatusWithTimeout();

            if (!allChecksOk) {
                throw new RuntimeException("Too many retries while trying to merge " + url + ". Merge aborted");
            }

            LOG.lifecycle("All checks passed! Merging pull request in repository '{}' between base = '{}' and head = '{}'.", task.getUpstreamRepositoryName(), task.getVersionUpgrade().getBaseBranch(), headBranch);
            gitHubApi.post("/repos/" + task.getUpstreamRepositoryName() + "/merges", body);
        } catch (Exception e) {
            throw new GradleException(String.format("Exception happen while trying to merge pull request. Merge aborted. Original issue: %s", e.getMessage()), e);
        }
    }
}
