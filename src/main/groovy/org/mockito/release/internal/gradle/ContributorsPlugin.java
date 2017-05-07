package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.mockito.release.gradle.ReleaseConfiguration;
import org.mockito.release.gradle.contributors.ConfigureContributorsTask;
import org.mockito.release.internal.gradle.util.Specs;
import org.mockito.release.internal.gradle.util.TaskMaker;
import org.mockito.release.notes.contributors.Contributors;
import org.mockito.release.version.VersionInfo;

import java.io.File;

import static org.mockito.release.internal.gradle.configuration.DeferredConfiguration.deferredConfiguration;

/**
 * Adds and configures tasks for getting contributor git user to GitHub user mappings.
 * Useful for release notes and pom.xml generation. Adds tasks:
 * <ul>
 *     <li>fetchLastContributorsFromGitHub - {@link RecentContributorsFetcherTask}</li>
 *     <li>fetchAllContributors - {@link AllContributorsFetcherTask}</li>
 *     <li>configureContributors - {@link AllContributorsFetcherTask}</li>
 * </ul>
 */
public class ContributorsPlugin implements Plugin<Project> {

    public final static String FETCH_CONTRIBUTORS_TASK = "fetchContributors";
    public final static String CONFIGURE_CONTRIBUTORS_TASK = "configureContributors";

    public void apply(final Project project) {
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();

        createTaskFetchLastContributorsFromGitHub(project, conf);

        createTaskFetchAllProjectContributorsFromGitHub(project, conf);
    }

    private void createTaskFetchLastContributorsFromGitHub(final Project project, final ReleaseConfiguration conf) {
        project.getTasks().create("fetchLastContributorsFromGitHub", RecentContributorsFetcherTask.class, new Action<RecentContributorsFetcherTask>() {
            @Override
            public void execute(final RecentContributorsFetcherTask task) {
                task.setGroup(TaskMaker.TASK_GROUP);
                task.setDescription("Fetch info about last contributors from GitHub and store it in file");

                final String toRevision = "HEAD";
                task.setToRevision(toRevision);

                deferredConfiguration(project, new Runnable() {
                    public void run() {
                        //TODO more and more tasks and plugins depend on VersionInfo.
                        //we should consider making it a part of release configuration in similar way we do 'releasing.notableRepo'
                        String fromRevision = "v" + project.getExtensions().getByType(VersionInfo.class).getPreviousVersion();
                        File contributorsFile = lastContributorsFile(project, fromRevision, toRevision);

                        task.setReadOnlyAuthToken(conf.getGitHub().getReadOnlyAuthToken());
                        task.setRepository(conf.getGitHub().getRepository());
                        task.setFromRevision(fromRevision);
                        task.setOutputFile(contributorsFile);
                    }
                });
            }
        });
    }

    private void createTaskFetchAllProjectContributorsFromGitHub(final Project project, final ReleaseConfiguration conf) {
        final AllContributorsFetcherTask fetcher = project.getTasks().create(FETCH_CONTRIBUTORS_TASK, AllContributorsFetcherTask.class, new Action<AllContributorsFetcherTask>() {
            @Override
            public void execute(final AllContributorsFetcherTask task) {
                task.setGroup(TaskMaker.TASK_GROUP);
                task.setDescription("Fetch info about all project contributors from GitHub and store it in file");
                task.setOutputFile(new File(project.getBuildDir(), "release-tools/project-contributors.json"));

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
                        " the serialized contributors data fetched earlier by " + FETCH_CONTRIBUTORS_TASK);
                t.dependsOn(fetcher);
                t.setContributorsData(fetcher.getOutputFile());
                t.setReleaseConfiguration(conf);
                t.onlyIf(Specs.fileExists(fetcher.getOutputFile()));
            }
        });
    }

    private File lastContributorsFile(Project project, String fromRevision, String toRevision) {
        String contributorsFileName = Contributors.getLastContributorsFileName(
                project.getBuildDir().getAbsolutePath(), fromRevision, toRevision);
        return new File(contributorsFileName);
    }
}


