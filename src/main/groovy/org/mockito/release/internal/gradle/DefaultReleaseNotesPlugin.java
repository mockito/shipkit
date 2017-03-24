package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.mockito.release.gradle.ReleaseNotesPlugin;

import static org.mockito.release.internal.gradle.CommonSettings.TASK_GROUP;

/**
 * --------------------------
 * ******* IMPORTANT ********
 * --------------------------
 *
 * Please update the documentation in the {@link ReleaseNotesPlugin} interface
 * when you make changes to this implementation
 * (for example: adding new tasks, renaming existing tasks, etc.).
 */
public class DefaultReleaseNotesPlugin implements ReleaseNotesPlugin {

    private final static String EXTENSION_NAME = "notes";

    public void apply(final Project project) {
        final DefaultReleaseNotesExtension notes = new DefaultReleaseNotesExtension(project.getProjectDir(), EXTENSION_NAME);

        //TODO those should be task classes with decent API
        project.getTasks().create("updateReleaseNotes", new Action<Task>() {
            public void execute(Task task) {
                task.setGroup(TASK_GROUP);
                task.setDescription("Updates release notes file.");
                task.doLast(new Action<Task>() {
                    public void execute(Task task) {
                        configureNotes(notes, project);
                        notes.updateReleaseNotes(project.getVersion().toString());
                    }
                });
            }
        });

        project.getTasks().create("previewReleaseNotes", new Action<Task>() {
            public void execute(Task task) {
                task.setGroup(TASK_GROUP);
                task.setDescription("Shows new incremental content of release notes. Useful for previewing the release notes.");
                task.doLast(new Action<Task>() {
                    public void execute(Task task) {
                        configureNotes(notes, project);
                        String content = notes.getReleaseNotes(project.getVersion().toString());
                        task.getLogger().lifecycle("----------------\n" + content + "----------------");
                    }
                });
            }
        });
    }

    private static void configureNotes(DefaultReleaseNotesExtension notes, Project project) {
        ExtContainer ext = new ExtContainer(project);
        notes.setGitHubLabelMapping(ext.getMap("releaseNotes_labelMapping"));
        notes.setGitHubReadOnlyAuthToken(ext.getString("gh_readOnlyAuthToken"));
        notes.setGitHubRepository(ext.getString("gh_repository"));
        notes.setReleaseNotesFile(project.file(ext.getString("releaseNotes_file")));
        notes.assertConfigured();

        //TODO make use of: ext.gh_writeAuthTokenEnvName = "GH_WRITE_TOKEN"
    }
}
