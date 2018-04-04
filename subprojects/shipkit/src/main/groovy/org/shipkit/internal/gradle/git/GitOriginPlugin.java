package org.shipkit.internal.gradle.git;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin;
import org.shipkit.internal.gradle.git.tasks.IdentifyGitOriginRepoTask;
import org.shipkit.internal.gradle.util.ProjectUtil;
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
        ProjectUtil.requireRootProject(project, this.getClass(), "This is needed so that we don't invoke git commands multiple times, per each submodule.");
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
