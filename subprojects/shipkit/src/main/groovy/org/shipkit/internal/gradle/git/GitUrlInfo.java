package org.shipkit.internal.gradle.git;

import org.shipkit.gradle.configuration.ShipkitConfiguration;

import static org.shipkit.internal.gradle.git.GitHubUrlBuilder.getGitHubUrl;

public class GitUrlInfo {

    private final String gitUrl;
    private final String writeToken;

    public GitUrlInfo(ShipkitConfiguration conf, String repository) {
        gitUrl = getGitHubUrl(repository, conf);
        writeToken = conf.getLenient().getGitHub().getWriteAuthToken();
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public String getWriteToken() {
        return writeToken;
    }
}
