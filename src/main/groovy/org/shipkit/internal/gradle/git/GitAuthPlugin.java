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

    private GitAuth gitAuth;

    @Override
    public void apply(Project project) {
        ShipkitConfiguration conf = project.getPlugins().apply(ShipkitConfigurationPlugin.class).getConfiguration();
        String writeToken = conf.getLenient().getGitHub().getWriteAuthToken();

        String url = getGitHubUrl(conf.getGitHub().getRepository(), conf);
        gitAuth = new GitAuth(url, writeToken);
    }

    static String getGitHubUrl(String ghRepo, ShipkitConfiguration conf) {
        String ghUser = conf.getGitHub().getWriteAuthUser();
        String writeToken = conf.getLenient().getGitHub().getWriteAuthToken();

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
        private final String repositoryUrl;
        private final String secretValue;

        GitAuth(String repositoryUrl, String secretValue) {
            this.repositoryUrl = repositoryUrl;
            this.secretValue = secretValue;
        }

        /**
         * Secret value to replace in {@link #repositoryUrl}.
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
        public String getRepositoryUrl() {
            return repositoryUrl;
        }
    }
}
