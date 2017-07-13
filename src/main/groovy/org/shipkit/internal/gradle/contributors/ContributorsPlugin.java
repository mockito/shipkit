package org.shipkit.internal.gradle.contributors;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.gradle.ReleaseConfiguration;
import org.shipkit.internal.gradle.configuration.ReleaseConfigurationPlugin;
import org.shipkit.internal.gradle.util.TaskMaker;

import static org.shipkit.internal.gradle.util.BuildConventions.contributorsFile;

/**
 * Adds and configures tasks for getting contributor information from GitHub.
 * Contributors information feeds release notes and pom.xml.
 * <p>
 * Applies plugins:
 * <ul>
 *     <li>{@link ReleaseConfigurationPlugin}</li>
 * </ul>
 *
 * Adds tasks:
 * <ul>
 *     <li>fetchAllContributors - {@link AllContributorsFetcherTask}</li>
 * </ul>
 */
public class ContributorsPlugin implements Plugin<Project> {

    public final static String FETCH_ALL_CONTRIBUTORS_TASK = "fetchAllContributors";

    public void apply(final Project project) {
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();
        fetchAllTask(project, conf);
    }

    private void fetchAllTask(final Project project, final ReleaseConfiguration conf) {
        project.getTasks().create(FETCH_ALL_CONTRIBUTORS_TASK, AllContributorsFetcherTask.class, new Action<AllContributorsFetcherTask>() {
            @Override
            public void execute(final AllContributorsFetcherTask task) {
                task.setGroup(TaskMaker.TASK_GROUP);
                task.setDescription("Fetch info about all project contributors from GitHub and store it in file");
                task.setOutputFile(contributorsFile(project));
                task.setApiUrl(conf.getGitHub().getApiUrl());
                task.setReadOnlyAuthToken(conf.getGitHub().getReadOnlyAuthToken());
                task.setRepository(conf.getGitHub().getRepository());
                task.setEnabled(conf.getTeam().getContributors().isEmpty());
            }
        });

    }
}


