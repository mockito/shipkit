package org.shipkit.internal.gradle.contributors.github;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.gradle.ReleaseConfiguration;
import org.shipkit.gradle.notes.FetchContributorsTask;
import org.shipkit.internal.gradle.configuration.ReleaseConfigurationPlugin;
import org.shipkit.internal.gradle.contributors.ContributorsPlugin;
import org.shipkit.internal.notes.contributors.ContributorsProvider;
import org.shipkit.internal.notes.contributors.github.Contributors;

/**
 * Adds and configures tasks for getting contributor information from GitHub.
 * Intended to be applied to the root project of your Gradle multi-project build.
 * <p>
 * Applies following plugins:
 * <ul>
 *     <li>{@link ReleaseConfigurationPlugin}</li>
 *     <li>{@link ContributorsPlugin}</li>
 * </ul>
 */
public class GithubContributorsPlugin implements Plugin<Project> {

    public void apply(final Project project) {
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();
        project.getPlugins().apply(ContributorsPlugin.class);

        final FetchContributorsTask task = (FetchContributorsTask) project.getTasks().getByName(ContributorsPlugin.FETCH_ALL_CONTRIBUTORS_TASK);
        configureGithub(conf, task);
    }

    private void configureGithub(ReleaseConfiguration conf, FetchContributorsTask task) {
        task.setDescription("Fetch info about all project contributors from GitHub and store it in file");
        task.setApiUrl(conf.getGitHub().getApiUrl());
        task.setReadOnlyAuthToken(conf.getGitHub().getReadOnlyAuthToken());
        task.setRepository(conf.getGitHub().getRepository());

        ContributorsProvider contributorsProvider = Contributors.getGitHubContributorsProvider(
            task.getApiUrl(), task.getRepository(), task.getReadOnlyAuthToken());
        task.setContributorsProvider(contributorsProvider);
    }
}
