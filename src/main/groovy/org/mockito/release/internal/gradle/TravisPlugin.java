package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.mockito.release.internal.gradle.util.TaskMaker;

/**
 * Configures the release automation to be used with Travis CI.
 * Adds tasks:
 *
 * <ul>
 *     <li>'travisReleasePrepare' - Prepares the working copy for releasing using Travis CI</li>
 * </ul>
 */
public class TravisPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(GitPlugin.class);

        TaskMaker.task(project, "travisReleasePrepare", new Action<Task>() {
            public void execute(Task t) {
                t.setDescription("Prepares the working copy for releasing using Travis CI");
                t.dependsOn(GitPlugin.UNSHALLOW_TASK, GitPlugin.CHECKOUT_BRANCH_TASK, GitPlugin.SET_USER_TASK, GitPlugin.SET_EMAIL_TASK);
            }
        });
    }
}
