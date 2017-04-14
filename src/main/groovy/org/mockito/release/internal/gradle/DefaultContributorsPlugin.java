package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.mockito.release.internal.gradle.util.CommonSettings;
import org.mockito.release.internal.gradle.util.ExtContainer;
import org.mockito.release.internal.gradle.util.FileUtil;
import org.mockito.release.notes.Notes;
import org.mockito.release.notes.contributors.Contributors;

import java.io.File;

public class DefaultContributorsPlugin implements Plugin<Project> {

    @Override
    public void apply(final Project project) {
        final ExtContainer ext = new ExtContainer(project);

        project.getTasks().create("fetchContributorsFromGitHub", ContributorsFetcherTask.class, new Action<ContributorsFetcherTask>() {
            @Override
            public void execute(final ContributorsFetcherTask task) {
                task.setGroup(CommonSettings.TASK_GROUP);
                task.setDescription("Fetch info about last contributors from GitHub and store it in file");

                final String toRevision = "HEAD";
                task.setToRevision(toRevision);

                task.doFirst(new Action<Task>() {
                    @Override
                    public void execute(Task t) {
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


