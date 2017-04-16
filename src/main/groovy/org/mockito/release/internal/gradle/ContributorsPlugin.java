package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.mockito.release.internal.gradle.util.ExtContainer;
import org.mockito.release.internal.gradle.util.FileUtil;
import org.mockito.release.internal.gradle.util.LazyConfigurer;
import org.mockito.release.internal.gradle.util.TaskMaker;
import org.mockito.release.notes.Notes;
import org.mockito.release.notes.contributors.Contributors;

import java.io.File;

/**
 * Adds and configures tasks for getting contributor git user to GitHub user mappings.
 * Useful for release generation. Adds tasks:
 * <ul>
 *     <li>fetchContributorsFromGitHub - {@link ContributorsFetcherTask}</li>
 * </ul>
 */
public class ContributorsPlugin implements Plugin<Project> {

    public void apply(final Project project) {
        final ExtContainer ext = new ExtContainer(project);

        project.getTasks().create("fetchContributorsFromGitHub", ContributorsFetcherTask.class, new Action<ContributorsFetcherTask>() {
            @Override
            public void execute(final ContributorsFetcherTask task) {
                task.setGroup(TaskMaker.TASK_GROUP);
                task.setDescription("Fetch info about last contributors from GitHub and store it in file");

                final String toRevision = "HEAD";
                task.setToRevision(toRevision);

                LazyConfigurer.getConfigurer(project).configureLazily(task, new Runnable() {
                    public void run() {
                        String fromRevision = fromRevision(project, ext);
                        File contributorsFile = contributorsFile(project, fromRevision, toRevision);

                        task.setAuthToken(ext.getGitHubReadOnlyAuthToken());
                        task.setRepository(ext.getGitHubRepository());
                        task.setFromRevision(fromRevision);
                        task.setContributorsFile(contributorsFile);
                    }
                });
            }
        });

    }

    private String fromRevision(Project project, ExtContainer ext) {
        project.file(ext.getReleaseNotesFile());
        String firstLine = FileUtil.firstLine(project.file(ext.getReleaseNotesFile()));
        return "v" + Notes.previousVersion(firstLine).getPreviousVersion();
    }

    private File contributorsFile(Project project, String fromRevision, String toRevision) {
        String contributorsFileName = Contributors.getContributorsFileName(
                project.getBuildDir().getAbsolutePath(), fromRevision, toRevision);
        return new File(contributorsFileName);
    }
}


