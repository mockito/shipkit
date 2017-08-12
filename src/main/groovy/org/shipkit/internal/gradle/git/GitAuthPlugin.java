package org.shipkit.internal.gradle.git;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin;

import java.text.MessageFormat;

/**
 * This plugin is used for internal purposes, it does not add any user-visible, public behavior.
 * It identifies GitHub repository url and keeps it in the field on this plugin.
 * Applies plugins:
 * <ul>
 *     <li>{@link ShipkitConfigurationPlugin}</li>
 * </ul>
 */
public class GitAuthPlugin implements Plugin<Project> {

    private static final Logger LOG = Logging.getLogger(GitAuthPlugin.class);

    private GitAuth gitAuth;

    @Override
    public void apply(Project project) {
        ShipkitConfiguration conf = project.getPlugins().apply(ShipkitConfigurationPlugin.class).getConfiguration();
        String ghUser = conf.getGitHub().getWriteAuthUser();
        String writeToken = conf.getLenient().getGitHub().getWriteAuthToken();

        String configUrl = getGitHubUrl(ghUser, conf.getGitHub().getRepository(), writeToken);
        String secretValue = null;
        //TODO we can push the lifecycle messages to the task execution (doFirst) and replace this plugin with simple convenience function
        if (writeToken != null) {
            LOG.lifecycle("  'git push/pull' use GitHub write token.");
            secretValue = writeToken;
        } else {
            LOG.lifecycle("  'git push/pull' do not use GitHub write token because it was not specified.");
        }
        gitAuth = new GitAuth(configUrl, secretValue);
    }

    static String getGitHubUrl(String ghUser, String ghRepo, String writeToken) {
        if(writeToken != null) {
            return MessageFormat.format("https://{0}:{1}@github.com/{2}.git", ghUser, writeToken, ghRepo);
        } else{
            return MessageFormat.format("https://github.com/{0}.git", ghRepo);
        }
    }

    public GitAuth getGitAuth(){
        return gitAuth;
    }

    public static class GitAuth{
        private final String configRepositoryUrl;
        private final String secretValue;

        public GitAuth(String configRepositoryUrl, String secretValue) {
            this.configRepositoryUrl = configRepositoryUrl;
            this.secretValue = secretValue;
        }

        /**
         * Secret value to replace in {@link #configRepositoryUrl}.
         * It may be null if {@link ShipkitConfiguration.GitHub#getWriteAuthToken()} is not specified.
         */
        public String getSecretValue() {
            return secretValue;
        }

        /**
         * URL of the GitHub repository, along with authentication data if it was specified.
         * Repository is based on {@link ShipkitConfiguration.GitHub#getRepository()}
         * It can be in one of the following formats:
         * - https://github.com/{repo}.git
         * - https://{ghUser}:{ghWriteToken}@github.com/{repo}.git
         */
        public String getConfigRepositoryUrl() {
            return configRepositoryUrl;
        }
    }
}
