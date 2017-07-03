package org.shipkit.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.shipkit.internal.gradle.git.GitPlugin;
import org.shipkit.internal.gradle.util.TaskMaker;

/**
 *  Combines plugins that allow updating release notes and pushing the results to the repository
 *
 *  * Applies following plugins and preconfigures tasks provided by those plugins:
 *
 * <ul>
 *     <li>{@link GitPlugin}</li>
 *     <li>{@link ReleaseNotesPlugin}</li>
 * </ul>
 */
public class AutoReleaseNotesPlugin implements Plugin<Project> {

    private static final String PERFORM_RELEASE_NOTES_UPDATE = "performReleaseNotesUpdate";

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(GitPlugin.class);
        project.getPlugins().apply(ReleaseNotesPlugin.class);

        TaskMaker.task(project, PERFORM_RELEASE_NOTES_UPDATE, new Action<Task>() {
            public void execute(Task t) {
                t.setDescription("Updates release notes, commits and pushes changes to Git repository");
                t.dependsOn(ReleaseNotesPlugin.UPDATE_NOTES_TASK);
                t.dependsOn(GitPlugin.PERFORM_GIT_PUSH_TASK);
            }
        });
    }
}
