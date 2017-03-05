package org.mockito.release.gradle.notes;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

/**
 * The plugin adds following tasks:
 *
 * <ul>
 *     <li>updateReleaseNotes - updates release notes file in place.</li>
 *     <li>previewReleaseNotes - prints incremental release notes to the console for preview.</li>
 * </ul>
 *
 * The plugin also adds "notes" extension:
 *
 * <pre>
 *  notes {
 *    notesFile = file("docs/release-notes.md")
 *    gitHubAuthToken = "secret"
 *    gitHubLabelMappings = [:]
 *  }
 * </pre>
 */
public class ReleaseNotesPlugin implements Plugin<Project> {

    public void apply(Project project) {
        final ReleaseNotesExtension notes = project.getExtensions().create(ReleaseNotesExtension.EXT_NAME, ReleaseNotesExtension.class,
                project.getProjectDir(), project.getVersion().toString());
        project.getTasks().create("updateReleaseNotes", new Action<Task>() {
            public void execute(Task task) {
                task.setGroup("Release Notes");
                task.setDescription("Updates release notes file.");
                task.doLast(new Action<Task>() {
                    public void execute(Task task) {
                        notes.updateReleaseNotes();
                    }
                });
            }
        });

        project.getTasks().create("previewReleaseNotes", new Action<Task>() {
            public void execute(Task task) {
                task.setGroup("Release Notes");
                task.setDescription("Shows new incremental content of release notes. Useful for previewing the release notes.");
                task.doLast(new Action<Task>() {
                    public void execute(Task task) {
                        String content = notes.getReleaseNotes();
                        task.getLogger().lifecycle("----------------\n$content----------------");
                    }
                });
            }
        });
    }
}
