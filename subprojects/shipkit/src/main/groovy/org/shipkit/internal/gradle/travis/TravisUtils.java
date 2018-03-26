package org.shipkit.internal.gradle.travis;

import org.shipkit.gradle.configuration.ShipkitConfiguration;

public class TravisUtils {

    private static final String URL_PATTERN = "https://travis-ci.org/%s/builds/%s";

    public static String generateCommitMessage(ShipkitConfiguration conf, String travisCommitMessage, String travisBuildNumber) {
        if (travisCommitMessage == null) {
            return null;
        }
        String travisJobUrl = generateTravisBuildUrl(conf, travisBuildNumber);

        if (travisCommitMessage.contains("[ci skip]")) {
            return travisCommitMessage.replace(" [ci skip]", ". CI job: " + travisJobUrl + " [ci skip]");
        } else {
            return travisCommitMessage + ". CI job: " + travisJobUrl;
        }
    }

    private static String generateTravisBuildUrl(ShipkitConfiguration conf, String travisBuildNumber) {
        return String.format(URL_PATTERN, conf.getGitHub().getRepository(), travisBuildNumber);
    }
}
