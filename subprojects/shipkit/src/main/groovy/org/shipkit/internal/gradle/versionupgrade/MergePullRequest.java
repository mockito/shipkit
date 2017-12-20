package org.shipkit.internal.gradle.versionupgrade;

import java.io.IOException;

import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.json.simple.JsonArray;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
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

        String headBranch = getHeadBranch(task.getForkRepositoryName(), task.getVersionBranch());

        IncubatingWarning.warn("merge pull requests");
        LOG.lifecycle("  Waiting for status of a pull request in repository '{}' between base = '{}' and head = '{}'.", task.getUpstreamRepositoryName(), task.getVersionUpgrade().getBaseBranch(), headBranch);

        String sha = retrievePullRequestSha(task, gitHubApi, headBranch);

        boolean isPending = false;String body = "{" +
            "  \"head\": \"" + headBranch + "\"," +
            "  \"base\": \"" + task.getVersionUpgrade().getBaseBranch() + "\"" +
            "}";

        int timeouts = 0;

        try {
            while (!isPending) {
                if(timeouts > 20) throw new RuntimeException("Timeout");
                int waitTime = 10000 * timeouts;
                String statusesResponse = gitHubApi.get("/repos/" + task.getUpstreamRepositoryName() + "/statuses/" + sha);
                JsonArray statuses = Jsoner.deserialize(statusesResponse, new JsonArray());

                for (Object status : statuses) {
                    String state = ((JsonObject) status).getString("state");
                    isPending = stateResolver(state);
                }

                if (!isPending) {
                    gitHubApi.post("/repos/" + task.getUpstreamRepositoryName() + "/merges", body);
                }

                Thread.sleep(waitTime);
                timeouts++;
            }
        } catch (Exception e) {
            throw new GradleException();
        }
    }

    private String retrievePullRequestSha(MergePullRequestTask task, GitHubApi gitHubApi, String headBranch) throws IOException {
        String branchResponse = gitHubApi.get("/repos/" + task.getUpstreamRepositoryName() + "/branches/" + headBranch);
        JsonObject branch = Jsoner.deserialize(branchResponse, new JsonObject());
        JsonObject commit = (JsonObject) branch.get("commit");
        return commit.getString("sha");
    }


    private boolean stateResolver(String state){
        if(state.equals("success")) return true;
        if(state.equals("error")) throw new RuntimeException();
        if(state.equals("failure")) throw new RuntimeException();
        return false;
    }

    private String getHeadBranch(String forkRepositoryName, String headBranch) {
        return getUserOfForkRepo(forkRepositoryName) + ":" + headBranch;
    }

    private String getUserOfForkRepo(String forkRepositoryName) {
        return forkRepositoryName.split("/")[0];
    }
}
