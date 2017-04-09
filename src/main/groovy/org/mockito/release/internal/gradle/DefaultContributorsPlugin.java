package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.mockito.release.gradle.ContributorsPlugin;
import org.mockito.release.internal.gradle.util.CommonSettings;
import org.mockito.release.internal.gradle.util.ExtContainer;
import org.mockito.release.internal.gradle.util.FileUtil;
import org.mockito.release.notes.Notes;

public class DefaultContributorsPlugin implements ContributorsPlugin {

    @Override
    public void apply(final Project project) {
        final ExtContainer ext = new ExtContainer(project);

        project.getTasks().create("fetchContributorsFromGitHub", new Action<Task>() {
            @Override
            public void execute(Task task) {
                final DefaultContributorsExtension extension = new DefaultContributorsExtension();

                task.setGroup(CommonSettings.TASK_GROUP);
                task.setDescription("Fetch info about last contributors from GitHub and store it in file");

                task.doFirst(new Action<Task>() {
                    @Override
                    public void execute(Task task) {
                        extension.setAuthToken(ext.getGitHubReadOnlyAuthToken());
                        extension.setRepository(ext.getGitHubRepository());
                        extension.setBuildDir(project.getBuildDir());
                        extension.setWorkDir(project.getProjectDir());
                    }
                });

                task.doLast(new Action<Task>() {
                    public void execute(Task task) {
                        //TODO MS call method
                        extension.fetchContributorsFromGitHub(fromRevision(project, ext), toRevision());
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

    private String toRevision() {
        return "HEAD";
    }
}


