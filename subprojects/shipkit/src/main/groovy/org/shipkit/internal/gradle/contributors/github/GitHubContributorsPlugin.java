package org.shipkit.internal.gradle.contributors.github;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.gradle.notes.FetchGitHubContributorsTask;
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin;
import org.shipkit.internal.gradle.git.GitAuthPlugin;
import org.shipkit.internal.gradle.util.TaskMaker;

import static org.shipkit.internal.gradle.util.BuildConventions.contributorsFile;

/**
 * Adds and configures tasks for getting contributor information from GitHub.
 * Intended to be applied to the root project of your Gradle multi-project build.
 * <p>
 * Applies following plugins:
 * <ul>
 *     <li>{@link ShipkitConfigurationPlugin}</li>
 * </ul>
 *
 * Adds tasks:
 * <ul>
 *     <li>fetchContributors - {@link FetchGitHubContributorsTask}</li>
 * </ul>
 */
public class GitHubContributorsPlugin implements Plugin<Project> {

    public final static String FETCH_CONTRIBUTORS = "fetchContributors";

    public void apply(final Project project) {
        final ShipkitConfiguration conf = project.getPlugins().apply(ShipkitConfigurationPlugin.class).getConfiguration();

        final FetchGitHubContributorsTask task = TaskMaker.task(project, FETCH_CONTRIBUTORS, FetchGitHubContributorsTask.class, new Action<FetchGitHubContributorsTask>() {
            @Override
            public void execute(final FetchGitHubContributorsTask task) {
                task.setDescription("Fetch info about all project contributors and store it in file");
                task.setOutputFile(contributorsFile(project));
                task.setEnabled(conf.getTeam().getContributors().isEmpty());

            }
        });
        task.setDescription("Fetch info about all project contributors from GitHub and store it in file");
        task.setApiUrl(conf.getGitHub().getApiUrl());
        task.setReadOnlyAuthToken(conf.getGitHub().getReadOnlyAuthToken());

        project.getRootProject().getPlugins().apply(GitAuthPlugin.class).provideAuthTo(task, new Action<GitAuthPlugin.GitAuth>() {
            @Override
            public void execute(GitAuthPlugin.GitAuth gitAuth) {
                task.setRepository(gitAuth.getRepositoryName());
            }
        });
    }
}
