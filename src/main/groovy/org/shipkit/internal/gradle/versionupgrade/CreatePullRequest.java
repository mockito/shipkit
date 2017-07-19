package org.shipkit.internal.gradle.versionupgrade;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.internal.util.GitHubApi;

import java.io.IOException;

class CreatePullRequest {

    private static final Logger LOG = Logging.getLogger(CreatePullRequest.class);

    public void createPullRequest(CreatePullRequestTask task) throws IOException {
        createPullRequest(task, new GitHubApi(task.getGitHubApiUrl(), task.getAuthToken()));
    }

    public void createPullRequest(CreatePullRequestTask task, GitHubApi gitHubApi) throws IOException {
        if(task.isDryRun()){
            LOG.lifecycle("  Skipping pull request creation due to dryRun = true");
            return;
        }

        LOG.lifecycle("  Creating a pull request of title '{}' in repository '{}' between base = '{}' and head = '{}'.",
            getTitle(task), task.getRepositoryUrl(), task.getVersionUpgrade().getBaseBranch(), task.getHeadBranch());

        String body = "{" +
            "  \"title\": \"" + getTitle(task) + "\"," +
            "  \"body\": \"" + getMessage(task) + "\"," +
            "  \"head\": \"" + task.getHeadBranch() + "\"," +
            "  \"base\": \"" + task.getVersionUpgrade().getBaseBranch() + "\"" +
            "}";

        System.out.println(body);

        gitHubApi.post("/repos/" + task.getRepositoryUrl() + "/pulls", body);
    }

    private String getMessage(CreatePullRequestTask task){
        return String.format("This pull request was automatically created by Shipkit's" +
         " 'version-upgrade-customer' Gradle plugin (http://shipkit.org)." +
        " Please merge it so that you are using fresh version of '%s' dependency.",
            task.getVersionUpgrade().getDependencyName());
    }

    private String getTitle(CreatePullRequestTask task){
        VersionUpgradeConsumerExtension versionUpgrade = task.getVersionUpgrade();
        return String.format("Version of %s upgraded to %s", versionUpgrade.getDependencyName(), versionUpgrade.getNewVersion());
    }
}
