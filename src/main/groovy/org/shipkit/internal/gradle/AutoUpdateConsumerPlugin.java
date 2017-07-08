package org.shipkit.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.Exec;
import org.shipkit.internal.gradle.autoupdate.ReplaceVersionTask;
import org.shipkit.internal.gradle.configuration.LazyConfiguration;
import org.shipkit.internal.gradle.util.TaskMaker;

public class AutoUpdateConsumerPlugin implements Plugin<Project> {

    public static final String AUTO_UPDATE_CHECKOUT_BRANCH = "autoUpdateCheckoutBranch";
    public static final String AUTO_UPDATE_REPLACE_VERSION = "autoUpdateReplaceVersion";
    public static final String AUTO_UPDATE_GIT_COMMIT = "autoUpdateGitCommit";
    public static final String AUTO_UPDATE_GIT_PUSH = "autoUpdateGitPush";

    public static final String SHIPKIT_NEW_VERSION = "shipkitNewVersion";
    public static final String SHIPKIT_DEPENDENCY_FILE = "shipkitDependencyFile";
    public static final String SHIPKIT_DEPENDENCY_PATTERN = "shipkitDependencyPattern";
    public static final String DEPENDENCY_PATTERN_DEFAULT = "org.shipkit:shipkit:{VERSION}";
    public static final String DEPENDENCY_FILE_DEFAULT = "build.gradle";

    @Override
    public void apply(final Project project) {

        TaskMaker.execTask(project, AUTO_UPDATE_CHECKOUT_BRANCH, new Action<Exec>() {
            @Override
            public void execute(final Exec exec) {
                exec.setDescription("Creates a new branch and checks it out.");

                LazyConfiguration.lazyConfiguration(exec, new Runnable() {
                    @Override
                    public void run() {
                        exec.commandLine("git", "checkout", "-b", getBranchName(project));
                    }
                });
            }
        });

        TaskMaker.task(project, AUTO_UPDATE_REPLACE_VERSION, ReplaceVersionTask.class, new Action<ReplaceVersionTask>() {
            @Override
            public void execute(final ReplaceVersionTask task) {
                task.setDescription("Replaces Shipkit's dependency version in config file.");
                task.mustRunAfter(AUTO_UPDATE_CHECKOUT_BRANCH);

                LazyConfiguration.lazyConfiguration(task, new Runnable() {
                    @Override
                    public void run() {
                        task.setNewVersion(getShipkitNewVersion(project));
                        String file = getDependencyFile(project);
                        task.setConfigFile(project.file(file));
                        task.setDependencyPattern(getDependencyPattern(project));
                    }
                });
            }
        });

        TaskMaker.execTask(project, AUTO_UPDATE_GIT_COMMIT, new Action<Exec>() {
            @Override
            public void execute(final Exec exec) {
                exec.setDescription("Commits updated config file.");
                exec.mustRunAfter(AUTO_UPDATE_REPLACE_VERSION);

                LazyConfiguration.lazyConfiguration(exec, new Runnable() {
                    @Override
                    public void run() {
                        String file = getDependencyFile(project);
                        exec.commandLine("git", "commit", "-m", "Shipkit version updated to " + getShipkitNewVersion(project), file);
                    }
                });
            }
        });

        TaskMaker.execTask(project, AUTO_UPDATE_GIT_PUSH, new Action<Exec>() {
            @Override
            public void execute(final Exec exec) {
                exec.setDescription("Pushes updated config file to an update branch.");
                exec.mustRunAfter(AUTO_UPDATE_GIT_COMMIT);

                LazyConfiguration.lazyConfiguration(exec, new Runnable() {
                    @Override
                    public void run() {
                        exec.commandLine("git", "push", "-u", "origin", getBranchName(project));
                    }
                });
            }
        });

        TaskMaker.task(project, "performAutoUpdate", new Action<Task>() {
            @Override
            public void execute(Task task) {
                task.setDescription("Checkouts new version branch, updates Shipkit dependency in config file, commits and pushes.");
                task.dependsOn(AUTO_UPDATE_CHECKOUT_BRANCH);
                task.dependsOn(AUTO_UPDATE_REPLACE_VERSION);
                task.dependsOn(AUTO_UPDATE_GIT_COMMIT);
                task.dependsOn(AUTO_UPDATE_GIT_PUSH);
            }
        });
    }

    private String getDependencyPattern(Project project) {
        return getProperty(project, SHIPKIT_DEPENDENCY_PATTERN, DEPENDENCY_PATTERN_DEFAULT);
    }

    private String getDependencyFile(Project project) {
        return getProperty(project, SHIPKIT_DEPENDENCY_FILE, DEPENDENCY_FILE_DEFAULT);
    }

    private String getShipkitNewVersion(Project project) {
        return getProperty(project, SHIPKIT_NEW_VERSION, null);
    }

    private String getBranchName(Project project){
        return "shipkit-bumped-version-" + getShipkitNewVersion(project);
    }

    private String getProperty(Project project, String propertyName, String defaultValue){
        Object value = project.getProperties().get(propertyName);
        if(value == null){
            return defaultValue;
        }
        return value.toString();
    }
}
