package org.shipkit.internal.gradle.versionupgrade;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.internal.util.GitHubApi;
import org.shipkit.internal.util.IncubatingWarning;

import java.io.IOException;

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
        String headBranch = getHeadBranch(task.getForkRepositoryName(), task.getVersionBranch());

        IncubatingWarning.warn("creating pull requests");
        LOG.lifecycle("  Creating a pull request of title '{}' in repository '{}' between base = '{}' and head = '{}'.",
            getTitle(task), task.getUpstreamRepositoryName(), task.getVersionUpgrade().getBaseBranch(), headBranch);

        String body = "{" +
            "  \"title\": \"" + getTitle(task) + "\"," +
            "  \"body\": \"" + getMessage(task) + "\"," +
            "  \"head\": \"" + headBranch + "\"," +
            "  \"base\": \"" + task.getVersionUpgrade().getBaseBranch() + "\"" +
            "}";

        gitHubApi.post("/repos/" + task.getUpstreamRepositoryName() + "/pulls", body);
    }

    private String getMessage(CreatePullRequestTask task) {
        return String.format("This pull request was automatically created by Shipkit's" +
         " 'org.shipkit.upgrade-downstream' Gradle plugin (http://shipkit.org)." +
        " Please merge it so that you are using fresh version of '%s' dependency.",
            task.getVersionUpgrade().getDependencyName());
    }

    private String getTitle(CreatePullRequestTask task) {
        UpgradeDependencyExtension versionUpgrade = task.getVersionUpgrade();
        return String.format("Version of %s upgraded to %s", versionUpgrade.getDependencyName(), versionUpgrade.getNewVersion());
    }

    private String getHeadBranch(String forkRepositoryName, String headBranch) {
        return getUserOfForkRepo(forkRepositoryName) + ":" + headBranch;
    }

    private String getUserOfForkRepo(String forkRepositoryName) {
        return forkRepositoryName.split("/")[0];
    }
}
