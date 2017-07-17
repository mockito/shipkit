package org.shipkit.internal.gradle.contributors.github;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.gradle.ReleaseConfiguration;
import org.shipkit.gradle.notes.FetchContributorsTask;
import org.shipkit.internal.gradle.configuration.ReleaseConfigurationPlugin;
import org.shipkit.internal.gradle.contributors.ContributorsPlugin;
import org.shipkit.internal.notes.contributors.ContributorsProvider;
import org.shipkit.internal.notes.contributors.github.Contributors;


public class GithubContributorsPlugin implements Plugin<Project> {

    public void apply(final Project project) {
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();
        final FetchContributorsTask task = project.getPlugins().apply(ContributorsPlugin.class).getFetchContributorsTask();

        task.setDescription("Fetch info about all project contributors from GitHub and store it in file");
        task.setApiUrl(conf.getGitHub().getApiUrl());
        task.setReadOnlyAuthToken(conf.getGitHub().getReadOnlyAuthToken());
        task.setRepository(conf.getGitHub().getRepository());

        ContributorsProvider contributorsProvider = Contributors.getGitHubContributorsProvider(
            task.getApiUrl(), task.getRepository(), task.getReadOnlyAuthToken());
        task.setContributorsProvider(contributorsProvider);
    }
}
