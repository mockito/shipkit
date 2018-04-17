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
    private RetryManager retryManager;

    public GitHubStatusCheck(MergePullRequestTask task, GitHubApi gitHubApi, int amountOfRetries, long defaultInitialTimeout) {
        this.task = task;
        this.gitHubApi = gitHubApi;
        this.retryManager = RetryManager.customRetryValues(amountOfRetries, defaultInitialTimeout);
    }

    public GitHubStatusCheck(MergePullRequestTask task, GitHubApi gitHubApi) {
        this.task = task;
        this.gitHubApi = gitHubApi;
        this.retryManager = RetryManager.defaultRetryValues();
    }

    public PullRequestStatus checkStatusWithRetries() throws IOException, InterruptedException {
        while (retryManager.shouldRetry()) {
            JsonObject status = getStatusCheck(task, gitHubApi);
            // it might be the case that we are too fast and statuses are not available yet -> let's do at least
            // one retry in this case.
            if (retryManager.timeoutHappened() && isNullOrEmpty(status, "statuses")) {
                return PullRequestStatus.NO_CHECK_DEFINED;
            } else if (!isNullOrEmpty(status, "statuses") && allStatusesPassed(status)) {
                return PullRequestStatus.SUCCESS;
            } else {
                LOG.lifecycle("Pull Request checks still in pending state. {}", retryManager.describe());
                retryManager.waitNow(this::waitingMethod);
            }
        }
        return PullRequestStatus.TIMEOUT;
    }

    private boolean isNullOrEmpty(JsonObject status, String key) {
        return status.getCollection(key) == null || status.getCollection(key).size() == 0;
    }

    private JsonObject getStatusCheck(MergePullRequestTask task, GitHubApi gitHubApi) throws IOException {
        String relativeUrl = "/repos/" + task.getUpstreamRepositoryName() + "/commits/" + task.getPullRequestSha() + "/status";
        LOG.lifecycle("Using {} for status check", relativeUrl);
        String statusesResponse = gitHubApi.get(relativeUrl);
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

    private void waitingMethod(Long t) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(t));
        } catch (InterruptedException e) {
            LOG.lifecycle("Waiting interrupted");
        }
    }
}
