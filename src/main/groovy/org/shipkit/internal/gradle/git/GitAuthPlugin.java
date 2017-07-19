package org.shipkit.internal.gradle.git;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.gradle.ReleaseConfiguration;
import org.shipkit.internal.exec.DefaultProcessRunner;
import org.shipkit.internal.gradle.configuration.ReleaseConfigurationPlugin;
import org.shipkit.internal.notes.vcs.GitOriginRepoProvider;

import java.text.MessageFormat;

public class GitAuthPlugin implements Plugin<Project>{

    private static final Logger LOG = Logging.getLogger(GitAuthPlugin.class);

    private GitAuth gitAuth;

    @Override
    public void apply(Project project) {
        ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();
        String ghUser = conf.getGitHub().getWriteAuthUser();
        String writeToken = conf.getGitHub().getWriteAuthToken();

        String configUrl;
        String originUrl;
        String secretValue = null;
        String originRepositoryName = getOriginRepositoryName(project);
        if (writeToken != null) {
            LOG.lifecycle("  'git push/pull' use GitHub write token.");
            configUrl = getUrlWithAuth(ghUser, conf.getGitHub().getRepository(), writeToken);
            originUrl = getUrlWithAuth(ghUser, originRepositoryName, writeToken);
            secretValue = writeToken;
        } else {
            LOG.lifecycle("  'git push/pull' do not use GitHub write token because it was not specified.");
            configUrl = getUrl(conf.getGitHub().getRepository());
            originUrl = getUrl(originRepositoryName);
        }
        gitAuth = new GitAuth(configUrl, originUrl, secretValue, originRepositoryName);
    }

    private String getUrl(String ghRepo) {
        return MessageFormat.format("https://github.com/{0}.git", ghRepo);
    }

    private String getOriginRepositoryName(Project project) {
        DefaultProcessRunner runner = new DefaultProcessRunner(project.getProjectDir());
        GitOriginRepoProvider repoProvider = new GitOriginRepoProvider(runner);
        try{
            return repoProvider.getOriginGitRepo();
        } catch(Exception e){
            LOG.debug("Failed to get local git remote origin", e);
            return null;
        }
    }

    private String getUrlWithAuth(String ghUser, String ghRepo, String writeToken) {
        return MessageFormat.format("https://{0}:{1}@github.com/{2}.git", ghUser, writeToken, ghRepo);
    }

    public static class GitAuth{
        private final String configRepositoryUrl;
        private final String originRepositoryUrl;
        private final String secretValue;
        private final String originRepositoryName;

        public GitAuth(String configRepositoryUrl, String originRepositoryUrl, String secretValue, String originRepositoryName) {
            this.configRepositoryUrl = configRepositoryUrl;
            this.originRepositoryUrl = originRepositoryUrl;
            this.secretValue = secretValue;
            this.originRepositoryName = originRepositoryName;
        }

        /**
         * Secret value to replace in {@link #configRepositoryUrl}.
         * It may be null if {@link ReleaseConfiguration.GitHub#getWriteAuthToken()} is not specified.
         */
        public String getSecretValue() {
            return secretValue;
        }

        /**
         * URL of the GitHub repository along with authentication data if it was specified.
         * Repository is based on local git origin.
         * It can be in one of the following formats:
         * - https://github.com/{repo}.git
         * - https://{ghUser}:{ghWriteToken}@github.com/{repo}.git
         */
        public String getOriginRepositoryUrl() {
            return originRepositoryUrl;
        }

        /**
         * URL of the GitHub repository, along with authentication data if it was specified.
         * Repository is based on {@link ReleaseConfiguration.GitHub#getRepository()}
         * It can be in one of the following formats:
         * - https://github.com/{repo}.git
         * - https://{ghUser}:{ghWriteToken}@github.com/{repo}.git
         */
        public String getConfigRepositoryUrl() {
            return configRepositoryUrl;
        }

        public String getOriginRepositoryName() {
            return originRepositoryName;
        }
    }

    public GitAuth getGitAuth(){
        return gitAuth;
    }
}
