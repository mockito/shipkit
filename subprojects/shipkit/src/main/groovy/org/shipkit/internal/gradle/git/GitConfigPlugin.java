package org.shipkit.internal.gradle.git;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.gradle.exec.ShipkitExecTask;
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin;
import org.shipkit.internal.gradle.exec.ExecCommandFactory;
import org.shipkit.internal.gradle.util.TaskMaker;

public class GitConfigPlugin implements Plugin<Project> {

    public static final String SET_USER_TASK = "setGitUserName";
    public static final String SET_EMAIL_TASK = "setGitUserEmail";

    @Override
    public void apply(Project project) {
        final ShipkitConfiguration conf = project.getPlugins().apply(ShipkitConfigurationPlugin.class).getConfiguration();

        TaskMaker.task(project, SET_USER_TASK, ShipkitExecTask.class, new Action<ShipkitExecTask>() {
            public void execute(final ShipkitExecTask t) {
                t.setDescription("Overwrites local git 'user.name' with a generic name. Intended for CI.");
                t.execCommand(ExecCommandFactory.execCommand("Setting git user name",
                    "git", "config", "--local", "user.name", conf.getGit().getUser()));
            }
        });

        TaskMaker.task(project, SET_EMAIL_TASK, ShipkitExecTask.class, new Action<ShipkitExecTask>() {
            public void execute(final ShipkitExecTask t) {
                t.setDescription("Overwrites local git 'user.email' with a generic email. Intended for CI.");
                t.execCommand(ExecCommandFactory.execCommand("Setting git user email",
                    "git", "config", "--local", "user.email", conf.getGit().getEmail()));
            }
        });
    }
}
