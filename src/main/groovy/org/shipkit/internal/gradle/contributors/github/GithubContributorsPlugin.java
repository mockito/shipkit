package org.shipkit.internal.gradle.contributors.github;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.gradle.ReleaseConfiguration;
import org.shipkit.gradle.notes.GithubContributorsTask;
import org.shipkit.internal.gradle.configuration.ReleaseConfigurationPlugin;
import org.shipkit.internal.gradle.util.TaskMaker;

import static org.shipkit.internal.gradle.util.BuildConventions.contributorsFile;

/**
 * Adds and configures tasks for getting contributor information from GitHub.
 * Intended to be applied to the root project of your Gradle multi-project build.
 * <p>
 * Applies following plugins:
 * <ul>
 *     <li>{@link ReleaseConfigurationPlugin}</li>
 * </ul>
 *
 * Adds tasks:
 * <ul>
 *     <li>fetchAllContributors - {@link GithubContributorsTask}</li>
 * </ul>
 */
public class GithubContributorsPlugin implements Plugin<Project> {

    public final static String FETCH_ALL_CONTRIBUTORS_TASK = "fetchAllContributors";

    public void apply(final Project project) {
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();

        final GithubContributorsTask task = TaskMaker.task(project, FETCH_ALL_CONTRIBUTORS_TASK, GithubContributorsTask.class, new Action<GithubContributorsTask>() {
            @Override
            public void execute(final GithubContributorsTask task) {
                task.setDescription("Fetch info about all project contributors and store it in file");
                task.setOutputFile(contributorsFile(project));
                task.setEnabled(conf.getTeam().getContributors().isEmpty());

            }
        });
        configureGithub(conf, task);
    }

    private void configureGithub(ReleaseConfiguration conf, GithubContributorsTask task) {
        task.setDescription("Fetch info about all project contributors from GitHub and store it in file");
        task.setApiUrl(conf.getGitHub().getApiUrl());
        task.setReadOnlyAuthToken(conf.getGitHub().getReadOnlyAuthToken());
        task.setRepository(conf.getGitHub().getRepository());
    }
}
