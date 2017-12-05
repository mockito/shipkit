package org.shipkit.internal.gradle.git;

import org.gradle.api.*;
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin;
import org.shipkit.internal.gradle.git.tasks.IdentifyGitOriginRepoTask;
import org.shipkit.internal.gradle.util.TaskMaker;

/**
 * This plugin adds task that identifies GitHub repository url and name and keeps it in the field on this plugin.
 * Applies plugins:
 * <ul>
 * <li>{@link ShipkitConfigurationPlugin}</li>
 * </ul>
 *
 * Adds tasks:
 * <ul>
 *     <li>{@link IdentifyGitOriginRepoTask}</li>
 * </ul>
 */
public class GitOriginPlugin implements Plugin<Project> {

    public static final String IDENTIFY_GIT_ORIGIN_TASK = "identifyGitOrigin";

    private IdentifyGitOriginRepoTask identifyTask;

    @Override
    public void apply(Project project) {
        if (project.getParent() != null) {
            //TODO SF let's have shipkit configuration use the same pattern and add some unit tests
            throw new GradleException("Plugin '" + this.getClass().getSimpleName() + "' is intended to be applied only root project.\n" +
                "This is needed so that we don't invoke git commands multiple times, per each submodule.\n" +
                "Please apply this plugin to the root project instead of '" + project.getPath() + "'.");
        }
        identifyTask = TaskMaker.task(project, IDENTIFY_GIT_ORIGIN_TASK, IdentifyGitOriginRepoTask.class, new Action<IdentifyGitOriginRepoTask>() {
            public void execute(IdentifyGitOriginRepoTask t) {
                t.setDescription("Identifies current git origin repo.");
            }
        });

        //Due to gnarly dependencies between plugins and tasks, we really need to apply this plugin after we declared the task
        //Trust me, it's all good :-)
        project.getPlugins().apply(ShipkitConfigurationPlugin.class);
    }

    public void provideOriginRepo(Task t, final Action<String> originRepoName) {
        t.dependsOn(identifyTask);
        identifyTask.doLast(new Action<Task>() {
            public void execute(Task task) {
                originRepoName.execute(identifyTask.getRepository());
            }
        });
    }
}
