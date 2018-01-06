package org.shipkit.internal.gradle.util

import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.json.simple.JsonObject
import org.shipkit.internal.gradle.git.OpenPullRequest

class BranchUtils {

    private static final Logger LOG = Logging.getLogger(BranchUtils.class);

    static String getHeadBranch(String forkRepositoryName, String headBranch) {
        return getUserOfForkRepo(forkRepositoryName) + ":" + headBranch
    }

    private static String getUserOfForkRepo(String forkRepositoryName) {
        return forkRepositoryName.split("/")[0]
    }

    static OpenPullRequest getOpenPullRequest(JsonObject pullRequest, String filter) {
        JsonObject head = (JsonObject) pullRequest.get("head");
        String url = ((JsonObject) pullRequest).getString("url")
        String branchName = head.getString("ref")
        if (filter == null || branchName.matches(filter)) {
            LOG.lifecycle("  Found an open pull request with version upgrade on branch {}", branchName);

            return new OpenPullRequest(
                ref: head.getString("ref"),
                sha: head.getString("sha"),
                url: url
            )
        }
    }
}
