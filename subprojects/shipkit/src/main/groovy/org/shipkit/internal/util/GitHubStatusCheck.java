package org.shipkit.internal.util;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.shipkit.internal.gradle.versionupgrade.MergePullRequestTask;

import java.io.IOException;
import java.util.Collection;

public class GitHubStatusCheck {

    private static final Logger LOG = Logging.getLogger(GitHubStatusCheck.class);

    private MergePullRequestTask task;
    private GitHubApi gitHubApi;

    public GitHubStatusCheck(MergePullRequestTask task, GitHubApi gitHubApi) {
        this.task = task;
        this.gitHubApi = gitHubApi;
    }

    public boolean checkStatusWithTimeout() throws IOException, InterruptedException {
        int timeouts = 0;
        while (timeouts < 20) {
            if (statusCheck(task, gitHubApi)) {
                return true;
            } else {
                int waitTime = 10000 * timeouts;
                Thread.sleep(waitTime);
                timeouts++;
                LOG.lifecycle("Pull Request checks still in pending state. Waiting %d seconds...", waitTime / 1000);
            }
        }
        return false;
    }

    private boolean statusCheck(MergePullRequestTask task, GitHubApi gitHubApi) throws IOException {
        String statusesResponse = gitHubApi.get("/repos/" + task.getUpstreamRepositoryName() + "/commits/" + task.getPullRequestSha() + "/status");
        JsonObject status = Jsoner.deserialize(statusesResponse, new JsonObject());
        return stateResolver(status);
    }

    private boolean stateResolver(JsonObject status) {
        if (status.getString("state").equals("success")) {
            return true;
        }

        if (hasErrorStates(status)) {
            Collection<JsonObject> statuses = status.getCollection("statuses");

            JsonObject firstError = findFirstError(statuses);
            if (firstError != null) {
                throw new RuntimeException(String.format(
                    "Pull request %s cannot be merged. %s. You can check details here: %s",
                    task.getPullRequestUrl(),
                    firstError.getString("description"),
                    firstError.getString("targetUrl")));
            }
        }
        return false;
    }

    private JsonObject findFirstError(Collection<JsonObject> statuses) {
        if (statuses == null) {
            return null;
        }
        for (JsonObject statusDetails : statuses) {
            if (hasErrorStates(statusDetails)) {
                return statusDetails;
            }
        }
        return null;
    }

    private boolean hasErrorStates(JsonObject statusDetails) {
        return statusDetails != null &&
               statusDetails.getString("state") != null &&
               (statusDetails.getString("state").equals("error") ||
               statusDetails.getString("state").equals("failure"));
    }
}
