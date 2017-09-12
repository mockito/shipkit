package org.shipkit.internal.gradle.git;

import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.internal.util.ExposedForTesting;

import java.text.MessageFormat;

/**
 * Builds GitHub urls
 */
class GitHubUrlBuilder {

    /**
     * Builds GitHub url for the repository
     */
    static String getGitHubUrl(String ghRepo, ShipkitConfiguration conf) {
        return _getGitHubUrl(conf.getGitHub().getWriteAuthUser(), ghRepo, conf.getLenient().getGitHub().getWriteAuthToken());
    }

    /**
     * Don't use directly, instead call {@link #getGitHubUrl(String, ShipkitConfiguration)}.
     * This way, we use lenient configuration and don't fail when auth token is not provided in shipkit configuration.
     */
    @ExposedForTesting
    static String _getGitHubUrl(String ghUser, String ghRepo, String writeToken) {
        if (writeToken != null) {
            return MessageFormat.format("https://{0}:{1}@github.com/{2}.git", ghUser, writeToken, ghRepo);
        } else {
            return MessageFormat.format("https://github.com/{0}.git", ghRepo);
        }
    }
}
