package org.shipkit.internal.gradle.util;

import org.shipkit.gradle.configuration.ShipkitConfiguration;

public class CommitMessageUtils {
    private static final String URL_PATTERN = "https://travis-ci.org/%s/builds/%s";

    public static String generateCommitMessagePostfix(ShipkitConfiguration conf, String travisBuildNumber) {
        if (!StringUtil.isEmpty(travisBuildNumber)) {
            String travisJobUrl = generateTravisBuildUrl(conf, travisBuildNumber);
            return String.format("CI job: %s %s", travisJobUrl, conf.getGit().getCommitMessagePostfix());
        }

        return conf.getGit().getCommitMessagePostfix();
    }

    private static String generateTravisBuildUrl(ShipkitConfiguration conf, String travisBuildNumber) {
        return String.format(URL_PATTERN, conf.getGitHub().getRepository(), travisBuildNumber);
    }
}
