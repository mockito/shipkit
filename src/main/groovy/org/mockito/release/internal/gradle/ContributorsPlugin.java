package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.mockito.release.gradle.ReleaseConfiguration;
import org.mockito.release.internal.gradle.util.FileUtil;
import org.mockito.release.internal.gradle.util.TaskMaker;
import org.mockito.release.notes.Notes;
import org.mockito.release.notes.contributors.Contributors;

import java.io.File;

import static org.mockito.release.internal.gradle.configuration.DeferredConfiguration.deferredConfiguration;

/**
 * Adds and configures tasks for getting contributor git user to GitHub user mappings.
 * Useful for release notes and pom.xml generation. Adds tasks:
 * <ul>
 *     <li>fetchLastContributorsFromGitHub - {@link ContributorsFetcherTask}</li>
 *     <li>fetchAllProjectContributorsFromGitHub - {@link AllContributorsFetcherTask}</li>
 * </ul>
 */
public class ContributorsPlugin implements Plugin<Project> {

    public final static String FETCH_CONTRIBUTORS_TASK = "fetchAllProjectContributorsFromGitHub";

    public void apply(final Project project) {
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();

        createTaskFetchLastContributorsFromGitHub(project, conf);

        createTaskFetchAllProjectContributorsFromGitHub(project, conf);
    }

    private void createTaskFetchLastContributorsFromGitHub(final Project project, final ReleaseConfiguration conf) {
        project.getTasks().create("fetchLastContributorsFromGitHub", ContributorsFetcherTask.class, new Action<ContributorsFetcherTask>() {
            @Override
            public void execute(final ContributorsFetcherTask task) {
                task.setGroup(TaskMaker.TASK_GROUP);
                task.setDescription("Fetch info about last contributors from GitHub and store it in file");

                final String toRevision = "HEAD";
                task.setToRevision(toRevision);

                deferredConfiguration(project, new Runnable() {
                    public void run() {
                        String fromRevision = fromRevision(project, conf);
                        File contributorsFile = lastContributorsFile(project, fromRevision, toRevision);

                        task.setReadOnlyAuthToken(conf.getGitHub().getReadOnlyAuthToken());
                        task.setRepository(conf.getGitHub().getRepository());
                        task.setFromRevision(fromRevision);
                        task.setContributorsFile(contributorsFile);
                    }
                });
            }
        });
    }

    private void createTaskFetchAllProjectContributorsFromGitHub(final Project project, final ReleaseConfiguration conf) {
        project.getTasks().create(FETCH_CONTRIBUTORS_TASK, AllContributorsFetcherTask.class, new Action<AllContributorsFetcherTask>() {
            @Override
            public void execute(final AllContributorsFetcherTask task) {
                task.setGroup(TaskMaker.TASK_GROUP);
                task.setDescription("Fetch info about all project contributors from GitHub and store it in file");

                deferredConfiguration(project, new Runnable() {
                    @Override
                    public void run() {
                        File contributorsFile = allProjectContributorsFile(project);
                        task.setReadOnlyAuthToken(conf.getGitHub().getReadOnlyAuthToken());
                        task.setRepository(conf.getGitHub().getRepository());
                        task.setContributorsFile(contributorsFile);
                        task.setSkipTaskExecution(!conf.getTeam().isAddContributorsToPomFromGitHub());
                    }
                });
            }
        });
    }

    private String fromRevision(Project project, ReleaseConfiguration conf) {
        String firstLine = FileUtil.firstLine(project.file(conf.getReleaseNotes().getFile()));
        return "v" + Notes.previousVersion(firstLine).getPreviousVersion();
    }

    private File lastContributorsFile(Project project, String fromRevision, String toRevision) {
        String contributorsFileName = Contributors.getLastContributorsFileName(
                project.getBuildDir().getAbsolutePath(), fromRevision, toRevision);
        return new File(contributorsFileName);
    }

    private File allProjectContributorsFile(Project project) {
        String fileName = Contributors.getAllProjectContributorsFileName(project.getBuildDir().getAbsolutePath());
        return new File(fileName);
    }
}


