package org.shipkit.internal.util;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.json.simple.JsonObject;
import org.json.simple.Jsoner;
import org.shipkit.internal.gradle.git.domain.PullRequestStatus;
import org.shipkit.internal.gradle.versionupgrade.MergePullRequestTask;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class GitHubStatusCheck {

    private static final Logger LOG = Logging.getLogger(GitHubStatusCheck.class);

    private MergePullRequestTask task;
    private GitHubApi gitHubApi;
    private final int amountOfRetries;

    public GitHubStatusCheck(MergePullRequestTask task, GitHubApi gitHubApi, int amountOfRetries) {
        this.task = task;
        this.gitHubApi = gitHubApi;
        this.amountOfRetries = amountOfRetries;
    }

    public GitHubStatusCheck(MergePullRequestTask task, GitHubApi gitHubApi) {
        this.task = task;
        this.gitHubApi = gitHubApi;
        this.amountOfRetries = 20;
    }

    public PullRequestStatus checkStatusWithRetries() throws IOException, InterruptedException {
        int timeouts = 0;
        while (timeouts < amountOfRetries) {
            JsonObject status = getStatusCheck(task, gitHubApi);
            if (status.getCollection("statuses") == null || status.getCollection("statuses").size() == 0) {
                return PullRequestStatus.NO_CHECK_DEFINED;
            }

            if (allStatusesPassed(status)) {
                return PullRequestStatus.SUCCESS;
            } else {
                timeouts++;
                long waitTimeInMillis = TimeUnit.SECONDS.toMillis(10) * timeouts;
                LOG.lifecycle("Pull Request checks still in pending state. Waiting {} seconds...", TimeUnit.MILLISECONDS.toSeconds(waitTimeInMillis));
                Thread.sleep(waitTimeInMillis);
            }
        }
        return PullRequestStatus.TIMEOUT;
    }

    private JsonObject getStatusCheck(MergePullRequestTask task, GitHubApi gitHubApi) throws IOException {
        String statusesResponse = gitHubApi.get("/repos/" + task.getUpstreamRepositoryName() + "/commits/" + task.getPullRequestSha() + "/status");
        return Jsoner.deserialize(statusesResponse, new JsonObject());
    }

    private boolean allStatusesPassed(JsonObject status) {
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
