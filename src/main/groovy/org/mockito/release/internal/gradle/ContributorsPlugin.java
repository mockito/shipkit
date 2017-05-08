package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.mockito.release.gradle.ReleaseConfiguration;
import org.mockito.release.gradle.contributors.ConfigureContributorsTask;
import org.mockito.release.internal.gradle.util.Specs;
import org.mockito.release.internal.gradle.util.TaskMaker;

import java.io.File;

import static org.mockito.release.internal.gradle.configuration.DeferredConfiguration.deferredConfiguration;
import static org.mockito.release.internal.gradle.util.BuildConventions.outputFile;

/**
 * Adds and configures tasks for getting contributor git user to GitHub user mappings.
 * Useful for release notes and pom.xml generation. Adds tasks:
 * <ul>
 *     <li>fetchAllContributors - {@link AllContributorsFetcherTask}</li>
 *     <li>configureContributors - {@link ConfigureContributorsTask}</li>
 * </ul>
 */
public class ContributorsPlugin implements Plugin<Project> {

    public final static String FETCH_ALL_CONTRIBUTORS_TASK = "fetchAllContributors";
    public final static String CONFIGURE_CONTRIBUTORS_TASK = "configureContributors";

    public void apply(final Project project) {
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();
        fetchAllTask(project, conf);
    }

    private void fetchAllTask(final Project project, final ReleaseConfiguration conf) {
        final AllContributorsFetcherTask fetcher = project.getTasks().create(FETCH_ALL_CONTRIBUTORS_TASK, AllContributorsFetcherTask.class, new Action<AllContributorsFetcherTask>() {
            @Override
            public void execute(final AllContributorsFetcherTask task) {
                task.setGroup(TaskMaker.TASK_GROUP);
                task.setDescription("Fetch info about all project contributors from GitHub and store it in file");
                task.setOutputFile(outputFile(project, "all-contributors.json"));

                deferredConfiguration(project, new Runnable() {
                    @Override
                    public void run() {
                        task.setReadOnlyAuthToken(conf.getGitHub().getReadOnlyAuthToken());
                        task.setRepository(conf.getGitHub().getRepository());
                        task.setEnabled(conf.getTeam().getContributors().isEmpty());
                    }
                });
            }
        });

        TaskMaker.task(project, CONFIGURE_CONTRIBUTORS_TASK, ConfigureContributorsTask.class, new Action<ConfigureContributorsTask>() {
            public void execute(ConfigureContributorsTask t) {
                t.setDescription("Sets contributors to 'releasing.team.contributors' based on" +
                        " the serialized contributors data fetched earlier by " + FETCH_ALL_CONTRIBUTORS_TASK);
                t.dependsOn(fetcher);
                t.setContributorsData(fetcher.getOutputFile());
                t.setReleaseConfiguration(conf);
                t.onlyIf(Specs.fileExists(fetcher.getOutputFile()));
            }
        });
    }
}


