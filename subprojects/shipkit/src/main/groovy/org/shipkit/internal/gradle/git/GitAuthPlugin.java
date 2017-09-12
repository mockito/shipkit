package org.shipkit.internal.gradle.git;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin;
import org.shipkit.internal.gradle.git.tasks.IdentifyGitOriginRepoTask;
import org.shipkit.internal.gradle.util.TaskMaker;

import static org.shipkit.internal.gradle.git.GitHubUrlBuilder.getGitHubUrl;

/**
 * This plugin is used for internal purposes, it does not add any user-visible, public behavior.
 * It identifies GitHub repository url and name and keeps it in the field on this plugin.
 * Applies plugins:
 * <ul>
 * <li>{@link ShipkitConfigurationPlugin}</li>
 * </ul>
 *
 * Adds tasks:
 * <ul>
 *     <li>{@link IdentifyGitOriginRepoTask}</li>
 * </ul>
 */
public class GitAuthPlugin implements Plugin<Project> {

    public static final String IDENTIFY_GIT_ORIGIN_TASK = "identifyGitOrigin";

    private IdentifyGitOriginRepoTask identifyTask;

    @Override
    public void apply(Project project) {
        identifyTask = TaskMaker.task(project, IDENTIFY_GIT_ORIGIN_TASK, IdentifyGitOriginRepoTask.class, new Action<IdentifyGitOriginRepoTask>() {
            public void execute(IdentifyGitOriginRepoTask t) {
                t.setDescription("Identifies current git origin repo.");
            }
        });
        project.getPlugins().apply(ShipkitConfigurationPlugin.class);
    }

    public void provideAuthTo(Task t, final Action<GitAuth> action) {
        t.dependsOn(identifyTask);
        identifyTask.doLast(new Action<Task>() {
            public void execute(Task task) {
                ShipkitConfiguration conf = identifyTask.getProject().getPlugins().getPlugin(ShipkitConfigurationPlugin.class).getConfiguration();
                String repoUrl = getGitHubUrl(identifyTask.getOriginRepo(), conf);
                String writeToken = conf.getLenient().getGitHub().getWriteAuthToken();
                action.execute(new GitAuth(repoUrl, writeToken, identifyTask.getOriginRepo()));
            }
        });
    }

    //TODO it's not longer only auth... let's find a better name
    public static class GitAuth {

        private final String repositoryUrl;
        private final String secretValue;
        private final String repositoryName;

        GitAuth(String repositoryUrl, String secretValue, String repositoryName) {
            //TODO let's call secretValue by name, e.g write token :)
            this.repositoryUrl = repositoryUrl;
            this.secretValue = secretValue;
            this.repositoryName = repositoryName;
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

        /**
         * GitHub repository name
         */
        public String getRepositoryName() {
            return repositoryName;
        }
    }
}
