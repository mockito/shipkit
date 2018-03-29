package org.shipkit.internal.gradle.travis;

import org.shipkit.gradle.configuration.ShipkitConfiguration;

public class TravisUtils {

    private static final String URL_PATTERN = "https://travis-ci.org/%s/builds/%s";

    public static String generateCommitMessagePostfix(ShipkitConfiguration conf, String travisBuildNumber) {
        if (travisBuildNumber == null) {
            return conf.getGit().getCommitMessagePostfix();
        }
        String travisJobUrl = generateTravisBuildUrl(conf, travisBuildNumber);

        return String.format("CI job: %s %s", travisJobUrl, conf.getGit().getCommitMessagePostfix());
    }

    private static String generateTravisBuildUrl(ShipkitConfiguration conf, String travisBuildNumber) {
        return String.format(URL_PATTERN, conf.getGitHub().getRepository(), travisBuildNumber);
    }
}
