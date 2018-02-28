package org.shipkit.internal.gradle.util;

import org.json.simple.JsonObject;
import org.shipkit.internal.gradle.git.domain.PullRequest;

public class PullRequestUtils {

    public static PullRequest toPullRequest(JsonObject pullRequest) {
        JsonObject head = (JsonObject) pullRequest.get("head");
        String url = pullRequest.getString("url");
        int prNumber = pullRequest.getInteger("number");

        PullRequest openPullRequest = new PullRequest();
        openPullRequest.setRef(head.getString("ref"));
        openPullRequest.setSha(head.getString("sha"));
        openPullRequest.setNumber(prNumber);

        openPullRequest.setUrl(url);
        return openPullRequest;
    }

}
