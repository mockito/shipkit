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
        return _getGitHubUrl(conf.getGitHub().getUrl(), conf.getGitHub().getWriteAuthUser(), ghRepo, conf.getLenient().getGitHub().getWriteAuthToken());
    }

    /**
     * Don't use directly, instead call {@link #getGitHubUrl(String, ShipkitConfiguration)}.
     * This way, we use lenient configuration and don't fail when auth token is not provided in shipkit configuration.
     */
    @ExposedForTesting
    static String _getGitHubUrl(String ghUrl, String ghUser, String ghRepo, String writeToken) {
        
              
        if (writeToken != null) {
            String[] parts =  ghUrl.split("://"); 
            return MessageFormat.format("{0}://{1}:{2}@{3}/{4}.git",parts[0], ghUser, writeToken, parts[1], ghRepo);
        } else {
            return MessageFormat.format("https://github.com/{0}.git", ghRepo);
        }
    }
}
