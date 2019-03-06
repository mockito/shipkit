package org.shipkit.internal.gradle.javadoc;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Copy;
import org.gradle.jvm.tasks.Jar;
import org.shipkit.gradle.configuration.ShipkitConfiguration;
import org.shipkit.gradle.git.GitCommitTask;
import org.shipkit.gradle.git.GitPushTask;
import org.shipkit.internal.gradle.configuration.ShipkitConfigurationPlugin;
import org.shipkit.internal.gradle.git.GitCommitTaskFactory;
import org.shipkit.internal.gradle.git.GitPlugin;
import org.shipkit.internal.gradle.git.GitUrlInfo;
import org.shipkit.internal.gradle.git.tasks.CloneGitRepositoryTask;
import org.shipkit.internal.gradle.git.tasks.GitCheckOutTask;
import org.shipkit.internal.gradle.util.TaskMaker;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;

/**
 * TODO
 */
public class JavadocPlugin implements Plugin<Project> {

    private static final Logger LOG = Logging.getLogger(JavadocPlugin.class);
    private static final String CHECKOUT_JAVADOC_REPO_BRANCH = "checkoutJavadocRepoBranch";
    private static final String CLONE_JAVADOC_REPO = "cloneJavadocRepo";
    private static final String COMMIT_JAVADOC_TASK = "commitJavadoc";
    private static final String COPY_JAVADOC_STAGE_TO_REPO_DIR_TASK = "copyJavadocStageToRepoDir";
    private static final String COPY_JAVADOC_TO_STAGE_VERSION_DIR_TASK = "copyJavadocToStageVersionDir";
    private static final String COPY_JAVADOC_TO_STAGE_CURRENT_DIR_TASK = "copyJavadocToStageCurrentDir";
    private static final String PUSH_JAVADOC_TASK = "pushJavadoc";
    private static final String RELEASE_JAVADOC_TASK = "releaseJavadoc";
    private static final String REFRESH_CURRENT_JAVADOC_TASK = "refreshCurrentJavadoc";
    private static final String REFRESH_VERSION_JAVADOC_TASK = "refreshVersionJavadoc";

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(GitPlugin.class);

        ShipkitConfiguration conf = project.getPlugins().apply(ShipkitConfigurationPlugin.class).getConfiguration();
        ShipkitConfiguration lenientConf = conf.getLenient();
        String directory = lenientConf.getJavadoc().getRepositoryDirectory();
        String branch = lenientConf.getJavadoc().getRepositoryBranch();
        String javadocRepository = getJavadocRepository(conf);
        String gitHubUrl = conf.getGitHub().getUrl();

        CloneGitRepositoryTask cloneJavadocTask = TaskMaker.task(project, CLONE_JAVADOC_REPO, CloneGitRepositoryTask.class,
            task -> {
                task.setDescription("Clones Javadoc repo " + javadocRepository + " into a temporary directory.");
                task.setRepositoryUrl(gitHubUrl + "/" + javadocRepository);
                task.setTargetDir(new File(getJavadocRepoCloneDir(project)));
                task.setDepth(10);
                // TODO onlyIf { stagingDir not empty }
            });

        GitCheckOutTask checkoutJavadocRepoBranch = TaskMaker.task(project, CHECKOUT_JAVADOC_REPO_BRANCH, GitCheckOutTask.class, task -> {
            task.setDescription("Checkout branch in Javadoc repository");
            task.dependsOn(cloneJavadocTask);
            task.onlyIf(foo -> branch != null);
            task.setRev(branch);
            task.setNewBranch(false);
        });

        Set<Copy> copyToVersionTasks = new HashSet<>();
        Set<Copy> copyToCurrentTasks = new HashSet<>();

        project.getTasksByName("javadocJar", true).stream()
            .map(task -> (Jar) task)
            .forEach(javadocJarTask -> {
                Copy copyToVersionTask = TaskMaker.task(javadocJarTask.getProject(), COPY_JAVADOC_TO_STAGE_VERSION_DIR_TASK, Copy.class, copyTask -> {
                    copyTask.setDescription("Extracts contents of javadoc jar to the staging /version directory");
                    copyTask.dependsOn(javadocJarTask);
                    //build javadoc first so that if there is no javadoc we will avoid cloning
                    cloneJavadocTask.mustRunAfter(javadocJarTask);
                    copyTask.from(project.zipTree(javadocJarTask.getArchivePath()));
                    // TODO how? : note that we need to use Closure/Callable because 'baseName' can be set by user later
                    copyTask.into(getJavadocStageDir(project) + "/" + javadocJarTask.getBaseName() + "/" + project.getVersion());
                });
                copyToVersionTasks.add(copyToVersionTask);

                Copy copyToCurrentTask = TaskMaker.task(javadocJarTask.getProject(), COPY_JAVADOC_TO_STAGE_CURRENT_DIR_TASK, Copy.class, copyTask -> {
                    copyTask.setDescription("Extracts contents of javadoc jar to the staging /current directory");
                    copyTask.dependsOn(copyToVersionTask);
                    copyTask.from(copyToVersionTask.getDestinationDir());
                    // TODO how? : note that we need to use Closure/Callable because 'baseName' can be set by user later
                    copyTask.into(getJavadocStageDir(project) + "/" + javadocJarTask.getBaseName() + "/current");
                });
                copyToCurrentTasks.add(copyToCurrentTask);
            });

        Task refreshVersionJavadocTask = TaskMaker.task(project, REFRESH_VERSION_JAVADOC_TASK, task -> {
            task.dependsOn(copyToVersionTasks);
            task.setDescription("Copy Javadocs from all modules to the staging /version directory");
        });

        Task refreshCurrentJavadocTask = TaskMaker.task(project, REFRESH_CURRENT_JAVADOC_TASK, task -> {
            task.dependsOn(copyToCurrentTasks);
            task.setDescription("Copy Javadocs from all modules to the staging /current directory");
        });

        Copy copyStageToRepoDir = TaskMaker.task(project, COPY_JAVADOC_STAGE_TO_REPO_DIR_TASK, Copy.class, task -> {
            task.setDescription("Copy prepared Javadocs from stage directory to the repository directory");
            task.dependsOn(checkoutJavadocRepoBranch, refreshVersionJavadocTask, refreshCurrentJavadocTask);
            task.from(getJavadocStageDir(project));
            task.into(getJavadocRepoCloneDir(project, directory));
        });

        GitCommitTask commitJavadocTask = GitCommitTaskFactory.createGitCommitTask(project, COMMIT_JAVADOC_TASK,
            "Commit changes in Javadoc repository directory");

        commitJavadocTask.dependsOn(copyStageToRepoDir);
        String commitMessage = getCommitMessage(project, lenientConf);
        commitJavadocTask.addDirectory(getJavadocRepoCloneDir(project, directory), commitMessage);
        commitJavadocTask.setWorkingDir(getJavadocRepoCloneDir(project));

        GitPushTask pushJavadoc = TaskMaker.task(project, PUSH_JAVADOC_TASK, GitPushTask.class, task -> {
            task.setDescription("Pushes Javadocs to remote Javadoc repo.");
            task.dependsOn(commitJavadocTask);
            task.setDryRun(conf.isDryRun());
            task.setWorkingDir(getJavadocRepoCloneDir(project));

            task.setUrl(cloneJavadocTask.getRepositoryUrl());
            GitUrlInfo info = new GitUrlInfo(conf);
            task.setSecretValue(info.getWriteToken());
            LOG.lifecycle(" Create pushJavadoc task token: " + info.getWriteToken());
            if (branch != null) {
                task.getTargets().add(branch);
            }
        });

        TaskMaker.task(project, RELEASE_JAVADOC_TASK, task -> {
            task.setDescription("Clone Javadoc repository, copy Javadocs, commit and push");
            task.dependsOn(cloneJavadocTask, commitJavadocTask, pushJavadoc);
        });

        deleteBuildDIrInRootProjectWhenCleanTask(project);
    }

    private String getCommitMessage(Project project, ShipkitConfiguration lenientConf) {
        String commitMessage = lenientConf.getJavadoc().getCommitMessage();
        if (isNull(commitMessage)) {
            commitMessage = "Update current and ${version} Javadocs.";
        }
        commitMessage = commitMessage.replaceAll("\\$\\{version\\}", project.getVersion().toString());
        return commitMessage;
    }

    private String getJavadocRepository(ShipkitConfiguration conf) {
        String javadocRepository = conf.getLenient().getJavadoc().getRepository();
        if (javadocRepository == null) {
            javadocRepository = conf.getGitHub().getRepository() + "-javadoc";  // sensible default
        }
        return javadocRepository;
    }

    private String getJavadocRepoCloneDir(Project project) {
        return project.getRootProject().getBuildDir().getAbsolutePath() + "/javadoc-repo";
    }

    private String getJavadocRepoCloneDir(Project project, String subdirectory) {
        return getJavadocRepoCloneDir(project)
            + "/" + (subdirectory != null ? subdirectory : ".");
    }

    private String getJavadocStageDir(Project project) {
        return project.getRootProject().getBuildDir().getAbsolutePath() + "/javadoc-stage";
    }

    private void deleteBuildDIrInRootProjectWhenCleanTask(Project project) {
        Set<Task> cleanTasks = project.getTasksByName("clean", true);
        cleanTasks.forEach(task -> task.doLast(task1 -> {
            project.delete(project.getBuildDir().getAbsolutePath());
        }));
    }
}
