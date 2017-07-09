package org.shipkit.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Exec;
import org.shipkit.gradle.ReleaseConfiguration;
import org.shipkit.gradle.git.GitPushTask;
import org.shipkit.internal.gradle.versionupgrade.CreatePullRequestTask;
import org.shipkit.internal.gradle.versionupgrade.ReplaceVersionTask;
import org.shipkit.internal.gradle.configuration.LazyConfiguration;
import org.shipkit.internal.gradle.configuration.ReleaseConfigurationPlugin;
import org.shipkit.internal.gradle.git.GitCheckOutTask;
import org.shipkit.internal.gradle.git.GitPush;
import org.shipkit.internal.gradle.util.TaskMaker;
import org.shipkit.internal.util.EnvVariables;
import org.shipkit.internal.util.ExposedForTesting;

import javax.inject.Inject;

/**
 * BEWARE! This plugin is in incubating state, so its API may change in the future!
 * The plugin adds following tasks:
 *
 * <ul>
 *     <li>versionUpgradeCheckoutBaseBranch - checkouts base branch - the branch to which version upgrade should be applied through pull request</li>
 *     <li>versionUpgradeCheckoutVersionBranch - checkouts version branch - a new branch where version will be upgraded</li>
 *     <li>versionUpgradeReplaceVersion - replaces version in build file, using dependency pattern</li>
 *     <li>versionUpgradeGitCommit - commits replaced version</li>
 *     <li>versionUpgradeGitPush - pushes the commit to the version branch</li>
 *     <li>versionUpgradeCreatePullRequest - creates a pull request between base and version branches</li>
 *     <li>performVersionUpgrade - task aggregating all of the above</li>
 * </ul>
 *
 * Plugin should be used in client projects that want to have automated version upgrades of some other dependency, that use the producer version of this plugin.
 * Project with the producer plugin applied would then clone a fork of client project and run './gradlew performVersionUpgrade -PdependencyNewVersion=${VERSION}' on it.
 */
public class VersionUpgradeConsumerPlugin implements Plugin<Project> {

    private static final Logger LOG = Logging.getLogger(VersionUpgradeConsumerPlugin.class);

    public static final String VERSION_UPGRADE_CHECKOUT_BASE_BRANCH = "versionUpgradeCheckoutBaseBranch";
    public static final String VERSION_UPGRADE_CHECKOUT_VERSION_BRANCH = "versionUpgradeCheckoutVersionBranch";
    public static final String VERSION_UPGRADE_REPLACE_VERSION = "versionUpgradeReplaceVersion";
    public static final String VERSION_UPGRADE_GIT_COMMIT = "versionUpgradeGitCommit";
    public static final String VERSION_UPGRADE_GIT_PUSH = "versionUpgradeGitPush";
    public static final String VERSION_UPGRADE_CREATE_PULL_REQUEST = "versionUpgradeCreatePullRequest";
    public static final String PERFORM_VERSION_UPGRADE = "performVersionUpgrade";

    public static final String DEPENDENCY_NEW_VERSION = "dependencyNewVersion";
    public static final String DEPENDENCY_BUILD_FILE = "dependencyBuildFile";
    public static final String DEPENDENCY_PATTERN = "dependencyPattern";
    public static final String BASE_BRANCH = "baseBranch";

    public static final String DEPENDENCY_PATTERN_DEFAULT = "org.shipkit:shipkit:{VERSION}";
    public static final String BUILD_FILE_DEFAULT = "build.gradle";
    public static final String BASE_BRANCH_DEFAULT = "master";


    private EnvVariables envVariables;

    @Inject
    public VersionUpgradeConsumerPlugin(){
        this(new EnvVariables());
    }

    @ExposedForTesting
    public VersionUpgradeConsumerPlugin(EnvVariables envVariables){
        this.envVariables = envVariables;
    }

    @Override
    public void apply(final Project project) {
        LOG.lifecycle("Applying VersionUpgradeConsumerPlugin, beware that it's is INCUBATING state, so its API may change!");
        final ReleaseConfiguration conf = project.getPlugins().apply(ReleaseConfigurationPlugin.class).getConfiguration();

        TaskMaker.task(project, VERSION_UPGRADE_CHECKOUT_BASE_BRANCH, GitCheckOutTask.class, new Action<GitCheckOutTask>() {
            @Override
            public void execute(final GitCheckOutTask task) {
                task.setDescription("Checks out the base branch.");

                LazyConfiguration.lazyConfiguration(task, new Runnable() {
                    @Override
                    public void run() {
                        task.setRev(getBaseBranch(project));
                    }
                });
            }
        });

        TaskMaker.task(project, VERSION_UPGRADE_CHECKOUT_VERSION_BRANCH, GitCheckOutTask.class, new Action<GitCheckOutTask>() {
            @Override
            public void execute(final GitCheckOutTask task) {
                task.setDescription("Creates a new version branch and checks it out.");
                task.mustRunAfter(VERSION_UPGRADE_CHECKOUT_BASE_BRANCH);

                LazyConfiguration.lazyConfiguration(task, new Runnable() {
                    @Override
                    public void run() {
                        task.setRev(getVersionBranchName(project));
                        task.setNewBranch(true);
                    }
                });
            }
        });

        TaskMaker.task(project, VERSION_UPGRADE_REPLACE_VERSION, ReplaceVersionTask.class, new Action<ReplaceVersionTask>() {
            @Override
            public void execute(final ReplaceVersionTask task) {
                task.setDescription("Replaces dependency version in config file.");
                task.mustRunAfter(VERSION_UPGRADE_CHECKOUT_VERSION_BRANCH);

                LazyConfiguration.lazyConfiguration(task, new Runnable() {
                    @Override
                    public void run() {
                        task.setNewVersion(getShipkitNewVersion(project));
                        String file = getDependencyFile(project);
                        task.setBuildFile(project.file(file));
                        task.setDependencyPattern(getDependencyPattern(project));
                    }
                });
            }
        });

        TaskMaker.execTask(project, VERSION_UPGRADE_GIT_COMMIT, new Action<Exec>() {
            @Override
            public void execute(final Exec exec) {
                exec.setDescription("Commits updated config file.");
                exec.mustRunAfter(VERSION_UPGRADE_REPLACE_VERSION);

                LazyConfiguration.lazyConfiguration(exec, new Runnable() {
                    @Override
                    public void run() {
                        String file = getDependencyFile(project);
                        exec.commandLine("git", "commit", "-m", "Shipkit version updated to " + getShipkitNewVersion(project), file);
                    }
                });
            }
        });

        TaskMaker.task(project, VERSION_UPGRADE_GIT_PUSH, GitPushTask.class, new Action<GitPushTask>() {
            @Override
            public void execute(final GitPushTask task) {
                task.setDescription("Pushes updated config file to an update branch.");
                task.mustRunAfter(VERSION_UPGRADE_GIT_COMMIT);
                GitPush.setPushUrl(task, conf, envVariables.getenv("GH_WRITE_TOKEN"));
                task.setDryRun(conf.isDryRun());

                LazyConfiguration.lazyConfiguration(task, new Runnable() {
                    @Override
                    public void run() {
                        task.getTargets().add(getVersionBranchName(project));
                    }
                });
            }
        });

        TaskMaker.task(project, VERSION_UPGRADE_CREATE_PULL_REQUEST, CreatePullRequestTask.class, new Action<CreatePullRequestTask>() {
            @Override
            public void execute(final CreatePullRequestTask task) {
                task.setDescription("Creates a pull request from branch with version upgraded to master");
                task.mustRunAfter(VERSION_UPGRADE_GIT_PUSH);
                task.setGitHubApiUrl(conf.getGitHub().getApiUrl());
                task.setRepositoryUrl(conf.getGitHub().getRepository());

                LazyConfiguration.lazyConfiguration(task, new Runnable() {
                    @Override
                    public void run() {
                        task.setAuthToken(getGitHubWriteToken(conf));
                        task.setTitle(String.format("Shipkit version bumped to %s", getShipkitNewVersion(project)));
                        task.setHeadBranch(getVersionBranchName(project));
                        task.setBaseBranch(getBaseBranch(project));
                    }
                });
            }
        });

        TaskMaker.task(project, PERFORM_VERSION_UPGRADE, new Action<Task>() {
            @Override
            public void execute(Task task) {
                task.setDescription("Checkouts new version branch, updates Shipkit dependency in config file, commits and pushes.");
                task.dependsOn(VERSION_UPGRADE_CHECKOUT_BASE_BRANCH);
                task.dependsOn(VERSION_UPGRADE_CHECKOUT_VERSION_BRANCH);
                task.dependsOn(VERSION_UPGRADE_REPLACE_VERSION);
                task.dependsOn(VERSION_UPGRADE_GIT_COMMIT);
                task.dependsOn(VERSION_UPGRADE_GIT_PUSH);
                task.dependsOn(VERSION_UPGRADE_CREATE_PULL_REQUEST);
            }
        });
    }

    private String getDependencyPattern(Project project) {
        return getProperty(project, DEPENDENCY_PATTERN, DEPENDENCY_PATTERN_DEFAULT);
    }

    private String getDependencyFile(Project project) {
        return getProperty(project, DEPENDENCY_BUILD_FILE, BUILD_FILE_DEFAULT);
    }

    private String getShipkitNewVersion(Project project) {
        return getProperty(project, DEPENDENCY_NEW_VERSION, null);
    }

    private String getVersionBranchName(Project project){
        return "shipkit-version-bumped-" + getShipkitNewVersion(project);
    }

    private String getBaseBranch(Project project){
        return getProperty(project, BASE_BRANCH, BASE_BRANCH_DEFAULT);
    }

    private String getProperty(Project project, String propertyName, String defaultValue){
        Object value = project.getProperties().get(propertyName);
        if(value == null){
            return defaultValue;
        }
        return value.toString();
    }

    private String getGitHubWriteToken(ReleaseConfiguration conf){
        return GitPush.getWriteToken(conf, envVariables.getenv("GH_WRITE_TOKEN"));
    }
}
