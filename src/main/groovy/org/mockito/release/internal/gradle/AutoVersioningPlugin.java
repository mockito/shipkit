package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.mockito.release.internal.gradle.util.TaskMaker;

/**
 * Plugin uses bumping version in version.properties file done by VersioningPlugin
 * and additionally commits and pushes changes to Github repo
 * You can use task "bumpVersionAndPush" to achieve all that
 *
 * Applies following plugins and preconfigures tasks provided by those plugins:
 *
 * <ul>
 *     <li>{@link VersioningPlugin}</li>
 * </ul>
 *
 * Adds following tasks:
 *
 * <ul>
 *     <li>performVersionBump</li>
 * </ul>
 */
public class AutoVersioningPlugin implements Plugin<Project> {

    static final String PERFORM_VERSION_BUMP = "performVersionBump";

    public void apply(final Project project) {
        project.getPlugins().apply(GitPlugin.class);
        project.getPlugins().apply(VersioningPlugin.class);


        TaskMaker.task(project, PERFORM_VERSION_BUMP, new Action<Task>() {
            public void execute(Task t) {
                t.setDescription("Increments version number, commits and pushes changes to Git repository");
                t.dependsOn(VersioningPlugin.BUMP_VERSION_FILE_TASK);
                t.dependsOn(GitPlugin.PERFORM_GIT_PUSH_TASK);
            }
        });
    }
}
