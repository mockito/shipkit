package org.shipkit.internal.gradle.versionupgrade;

import java.io.IOException;

import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.shipkit.internal.gradle.util.BranchUtils;
import org.shipkit.internal.util.GitHubApi;
import org.shipkit.internal.util.IncubatingWarning;

class MergePullRequest {

    private static final Logger LOG = Logging.getLogger(MergePullRequest.class);

    public void mergePullRequest(MergePullRequestTask task) throws IOException {
        mergePullRequest(task, new GitHubApi(task.getGitHubApiUrl(), task.getAuthToken()));
    }

    public void mergePullRequest(MergePullRequestTask task, GitHubApi gitHubApi) throws IOException {
        if (task.isDryRun()) {
            LOG.lifecycle(" Skipping pull request merging due to dryRun = true");
            return;
        }

        String headBranch = BranchUtils.getHeadBranch(task.getForkRepositoryName(), task.getVersionBranch());

        IncubatingWarning.warn("merge pull requests");
        LOG.lifecycle("Waiting for status of a pull request in repository '{}' between base = '{}' and head = '{}'.", task.getUpstreamRepositoryName(), task.getVersionUpgrade().getBaseBranch(), headBranch);

        String sha = retrievePullRequestSha(task, gitHubApi, headBranch);
        boolean isPending = true;
        String body = "{" +
            "  \"head\": \"" + headBranch + "\"," +
            "  \"base\": \"" + task.getVersionUpgrade().getBaseBranch() + "\"" +
            "}";

        int timeouts = 0;

        try {
            while (isPending) {
                if (timeouts > 20) {
                    throw new RuntimeException("To many retries. Merge aborted");
                }

                String statusesResponse = gitHubApi.get("/repos/" + task.getUpstreamRepositoryName() + "/statuses/" + sha);
                JsonArray statuses = Jsoner.deserialize(statusesResponse, new JsonArray());

                for (Object status : statuses) {
                    String state = ((JsonObject) status).getString("state");
                    String description = ((JsonObject) status).getString("description");
                    isPending = stateResolver(state, description);
                }

                if (!isPending) {
                    LOG.lifecycle("All checks passed! Merging pull request in repository '{}' between base = '{}' and head = '{}'.", task.getUpstreamRepositoryName(), task.getVersionUpgrade().getBaseBranch(), headBranch);
                    gitHubApi.post("/repos/" + task.getUpstreamRepositoryName() + "/merges", body);
                } else {
                    int waitTime = 10000 * timeouts;
                    Thread.sleep(waitTime);
                    timeouts++;
                    LOG.lifecycle("Pull Request checks still in pending state. Waiting %d seconds...", waitTime / 1000);
                }
            }
        } catch (Exception e) {
            throw new GradleException(e.getMessage());
        }
    }

    private String retrievePullRequestSha(MergePullRequestTask task, GitHubApi gitHubApi, String headBranch) throws IOException {
        String branchResponse = gitHubApi.get("/repos/" + task.getUpstreamRepositoryName() + "/branches/" + headBranch);
        JsonObject branch = Jsoner.deserialize(branchResponse, new JsonObject());
        JsonObject commit = (JsonObject) branch.get("commit");
        return commit.getString("sha");
    }

    private boolean stateResolver(String state, String description) {
        if (state.equals("success")) {
            return false;
        }
        if (state.equals("error")) {
            throw new RuntimeException("Status of check '" + description + "':error. Merge aborted");
        }
        if (state.equals("failure")) {
            throw new RuntimeException("Status of check '" + description + "':failure. Merge aborted");
        }
        return true;
    }


}
