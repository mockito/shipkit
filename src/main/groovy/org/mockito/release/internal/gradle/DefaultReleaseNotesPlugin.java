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
        final DefaultReleaseNotesExtension notes = project.getExtensions().create(
                EXTENSION_NAME, DefaultReleaseNotesExtension.class,
                project.getProjectDir(), EXTENSION_NAME);

        //TODO those should be task classes with decent API
        project.getTasks().create("updateReleaseNotes", new Action<Task>() {
            public void execute(Task task) {
                task.setGroup(TASK_GROUP);
                task.setDescription("Updates release notes file.");
                task.doLast(new Action<Task>() {
                    public void execute(Task task) {
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
                        String content = notes.getReleaseNotes(project.getVersion().toString());
                        task.getLogger().lifecycle("----------------\n" + content + "----------------");
                    }
                });
            }
        });
    }
}
