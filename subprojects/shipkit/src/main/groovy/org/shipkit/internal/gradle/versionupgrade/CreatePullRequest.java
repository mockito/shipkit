package org.shipkit.internal.gradle.versionupgrade;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.internal.impldep.org.apache.commons.lang.StringUtils;
import org.shipkit.internal.util.GitHubApi;
import org.shipkit.internal.util.IncubatingWarning;

import java.io.IOException;

import static org.gradle.internal.impldep.org.apache.commons.lang.StringUtils.isBlank;

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

        checkPullRequestMetadata(task);

        String headBranch = getHeadBranch(task.getForkRepositoryName(), task.getVersionBranch());

        IncubatingWarning.warn("creating pull requests");
        LOG.lifecycle("  Creating a pull request of title '{}' in repository '{}' between base = '{}' and head = '{}'.",
            getTitle(task), task.getUpstreamRepositoryName(), task.getVersionUpgrade().getBaseBranch(), headBranch);

        String body = "{" +
            "  \"title\": \"" + getTitle(task) + "\"," +
            "  \"body\": \"" + getMessage(task) + "\"," +
            "  \"head\": \"" + headBranch + "\"," +
            "  \"base\": \"" + task.getVersionUpgrade().getBaseBranch() + "\"," +
            "  \"maintainer_can_modify\": true" +
            "}";

        gitHubApi.post("/repos/" + task.getUpstreamRepositoryName() + "/pulls", body);
    }

    private void checkPullRequestMetadata(CreatePullRequestTask task) {
        if (isBlank(getTitle(task))) {
            throw new IllegalArgumentException("Cannot create pull request for empty pull request title. Set it with git.pullRequestTitle property in configuration.");
        }

        if (isBlank(getMessage(task))) {
            throw new IllegalArgumentException("Cannot create pull request for empty pull request description. Set it with git.pullRequestDescription property in configuration.");
        }
    }

    private String getMessage(CreatePullRequestTask task) {
        return task.getPullRequestDescription();
    }

    private String getTitle(CreatePullRequestTask task) {
        return task.getPullRequestTitle();
    }

    private String getHeadBranch(String forkRepositoryName, String headBranch) {
        return getUserOfForkRepo(forkRepositoryName) + ":" + headBranch;
    }

    private String getUserOfForkRepo(String forkRepositoryName) {
        return forkRepositoryName.split("/")[0];
    }
}
