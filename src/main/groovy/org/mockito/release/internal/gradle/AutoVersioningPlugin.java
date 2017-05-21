package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.Exec;
import org.mockito.release.internal.gradle.util.TaskMaker;

import org.mockito.release.gradle.ReleaseConfiguration;
import org.mockito.release.gradle.SecureExecTask;
import org.mockito.release.internal.gradle.util.GitUtil;

import static org.mockito.release.internal.gradle.configuration.DeferredConfiguration.deferredConfiguration;
import static org.mockito.release.internal.gradle.configuration.LazyConfiguration.lazyConfiguration;

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
 *     <li>bumpVersionAndPush</li>
 * </ul>
 */
public class AutoVersioningPlugin implements Plugin<Project> {

    static final String BUMP_VERSION_AND_PUSH_TASK = "bumpVersionAndPush";

    public void apply(final Project project) {
        project.getPlugins().apply(GitPushPlugin.class);
        project.getPlugins().apply(VersioningPlugin.class);


        TaskMaker.task(project, BUMP_VERSION_AND_PUSH_TASK, new Action<Task>() {
            public void execute(Task t) {
                t.setDescription("Increments version number, commits and pushes changes to Git repository");
                t.dependsOn(VersioningPlugin.BUMP_VERSION_FILE_TASK);
                t.dependsOn(GitPushPlugin.PERFORM_GIT_PUSH_TASK);
            }
        });
    }
}
