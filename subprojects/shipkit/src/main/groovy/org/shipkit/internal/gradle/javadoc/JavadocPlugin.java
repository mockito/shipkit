package org.shipkit.internal.gradle.javadoc;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static org.shipkit.internal.gradle.release.ReleasePlugin.PERFORM_RELEASE_TASK;

/**
 * Release Javadoc to git repository.
 * <p>
 * Applies:
 *
 * <ul>
 *      <li>{@link GitPlugin}</li>
 *      <li>{@link ShipkitConfigurationPlugin}</li>
 * </ul>
 *
 * <p>
 * Adds tasks:
 *
 * <ul>
 *      <li>cloneJavadocRepo - clones Javadoc repository into a temporary directory. Javadoc will be published in this
 *          repository.</li>
 *      <li>checkoutJavadocRepoBranch - checkouts a branch in Javadoc repository, where Javadoc will be published.</li>
 *      <li>copyJavadocToStageVersionDir - extracts and copies Javadoc from Javadoc jar to stage directory to version
 *          (eg.: "/1.2.3") subdirectory. This task is added to each subproject.</li>
 *      <li>copyJavadocToStageCurrentDir - extracts and copies Javadoc from Javadoc jar to stage directory to /current
 *          current directory. This task is added to each subproject.</li>
 *      <li>refreshVersionJavadoc - aggregates all copyJavadocToStageVersionDir tasks.</li>
 *      <li>refreshCurrentJavadoc - aggregates all copyJavadocToStageCurrentDir tasks.</li>
 *      <li>copyJavadocStageToRepoDir - copies all Javadocs from stage directory to Javadoc repository directory.</li>
 *      <li>commitJavadoc - commits all changes in Javadoc repository.</li>
 *      <li>pushJavadoc - pushes Javadocs to remote Javadoc repository.</li>
 *      <li>releaseJavadoc - aggregate all tasks needed to release Javadocs: clone, checkout, copy, commit, push.</li>
 *      <li>clean - additionally removes build directory in project root</li>
 * </ul>
 */
public class JavadocPlugin implements Plugin<Project> {

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

        CloneGitRepositoryTask cloneJavadocTask = createCloneJavadocTask(project, conf);

        GitCheckOutTask checkoutJavadocRepoBranch = createCheckoutJavadocReposBranch(project, conf);
        checkoutJavadocRepoBranch.dependsOn(cloneJavadocTask);

        CopyTasks copyTasks = createCopyJavadocToStageTasks(project);
        Task refreshVersionJavadocTask = createRefreshVersionJavadocTask(project, copyTasks.copyToVersionTasks);
        Task refreshCurrentJavadocTask = createRefreshCurrentJavadocTask(project, copyTasks.copyToCurrentTasks);

        //refresh javadoc first so that if there is no javadoc we will avoid cloning
        cloneJavadocTask.mustRunAfter(refreshVersionJavadocTask);

        Copy copyStageToRepoDir = createCopyStageToRepoDirTask(project, conf);
        copyStageToRepoDir.dependsOn(checkoutJavadocRepoBranch, refreshVersionJavadocTask, refreshCurrentJavadocTask);

        GitCommitTask commitJavadocTask = createGitCommitTask(project, conf);
        commitJavadocTask.dependsOn(copyStageToRepoDir);

        GitPushTask pushJavadoc = createPushJavadocTask(project, conf);
        pushJavadoc.dependsOn(commitJavadocTask);

        Task releaseJavadocTask = createReleaseJavadocTask(project);
        releaseJavadocTask.dependsOn(cloneJavadocTask, commitJavadocTask, pushJavadoc);
        Task performReleaseTask = project.getRootProject().getTasks().findByName(PERFORM_RELEASE_TASK);
        if (performReleaseTask != null) {
            performReleaseTask.dependsOn(releaseJavadocTask);
        }

        deleteBuildDIrInRootProjectWhenCleanTask(project);
    }

    private CloneGitRepositoryTask createCloneJavadocTask(Project project, ShipkitConfiguration conf) {
        String javadocRepository = getJavadocRepository(conf);
        String gitHubUrl = conf.getGitHub().getUrl();
        return TaskMaker.task(project, CLONE_JAVADOC_REPO, CloneGitRepositoryTask.class,
            task -> {
                task.setDescription("Clones Javadoc repo " + javadocRepository + " into a temporary directory.");
                task.setRepositoryUrl(gitHubUrl + "/" + javadocRepository);
                task.setTargetDir(new File(getJavadocRepoCloneDir(project)));
                // onlyIf { stagingDir not empty }
                task.onlyIf(t -> containsFileInDir(getJavadocStageDir(project)));
            });
    }

    private GitCheckOutTask createCheckoutJavadocReposBranch(Project project, ShipkitConfiguration conf) {
        String branch = conf.getLenient().getJavadoc().getRepositoryBranch();
        return TaskMaker.task(project, CHECKOUT_JAVADOC_REPO_BRANCH, GitCheckOutTask.class, task -> {
            task.setDescription("Checkout branch in Javadoc repository");
            task.onlyIf(foo -> branch != null);
            task.setRev(branch);
            task.setNewBranch(false);
            task.setDirectory(new File(getJavadocRepoCloneDir(project)));
        });
    }

    private CopyTasks createCopyJavadocToStageTasks(Project project) {
        Set<Copy> copyToVersionTasks = new HashSet<>();
        Set<Copy> copyToCurrentTasks = new HashSet<>();

        project.getTasksByName("javadocJar", true).stream()
            .map(task -> (Jar) task)
            .forEach(javadocJarTask -> {
                Copy copyToVersionTask = TaskMaker.task(javadocJarTask.getProject(), COPY_JAVADOC_TO_STAGE_VERSION_DIR_TASK, Copy.class, copyTask -> {
                    copyTask.setDescription("Extracts contents of javadoc jar to the staging /version directory");
                    copyTask.dependsOn(javadocJarTask);
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
        return new CopyTasks(copyToVersionTasks, copyToCurrentTasks);
    }

    private Task createRefreshVersionJavadocTask(Project project, Set<Copy> copyToVersionTasks) {
        return TaskMaker.task(project, REFRESH_VERSION_JAVADOC_TASK, task -> {
            task.dependsOn(copyToVersionTasks);
            task.setDescription("Copy Javadocs from all modules to the staging /version directory");
        });
    }

    private Task createRefreshCurrentJavadocTask(Project project, Set<Copy> copyToCurrentTasks) {
        return TaskMaker.task(project, REFRESH_CURRENT_JAVADOC_TASK, task -> {
            task.dependsOn(copyToCurrentTasks);
            task.setDescription("Copy Javadocs from all modules to the staging /current directory");
        });
    }

    private Copy createCopyStageToRepoDirTask(Project project,
                                              ShipkitConfiguration conf) {
        String directory = conf.getLenient().getJavadoc().getRepositoryDirectory();

        return TaskMaker.task(project, COPY_JAVADOC_STAGE_TO_REPO_DIR_TASK, Copy.class, task -> {
            task.setDescription("Copy prepared Javadocs from stage directory to the repository directory");
            task.from(getJavadocStageDir(project));
            task.into(getJavadocRepoCloneDir(project, directory));
        });
    }

    private GitCommitTask createGitCommitTask(Project project, ShipkitConfiguration conf) {
        String directory = conf.getLenient().getJavadoc().getRepositoryDirectory();

        GitCommitTask commitJavadocTask = GitCommitTaskFactory.createGitCommitTask(project, COMMIT_JAVADOC_TASK,
            "Commit changes in Javadoc repository directory");

        String commitMessage = getCommitMessage(project, conf.getLenient());
        File file = new File(getJavadocRepoCloneDir(project, directory));
        commitJavadocTask.addChange(singletonList(file), commitMessage, null);
        commitJavadocTask.setWorkingDir(new File(getJavadocRepoCloneDir(project)));
        return commitJavadocTask;
    }

    private GitPushTask createPushJavadocTask(Project project, ShipkitConfiguration conf) {
        String branch = conf.getLenient().getJavadoc().getRepositoryBranch();
        String javadocRepository = getJavadocRepository(conf);

        return TaskMaker.task(project, PUSH_JAVADOC_TASK, GitPushTask.class, task -> {
            task.setDescription("Pushes Javadocs to remote Javadoc repository");
            task.setDryRun(conf.isDryRun());
            task.setWorkingDir(getJavadocRepoCloneDir(project));

            GitUrlInfo info = new GitUrlInfo(conf, javadocRepository);
            task.setUrl(info.getGitUrl());
            task.setSecretValue(info.getWriteToken());
            if (branch != null) {
                task.getTargets().add(branch);
            }
        });
    }

    private Task createReleaseJavadocTask(Project project) {
        return TaskMaker.task(project, RELEASE_JAVADOC_TASK, task -> {
            task.setDescription("Clone Javadoc repository, copy Javadocs, commit and push");
        });
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

    private boolean containsFileInDir(String directory) {
        try {
            return Files.walk(Paths.get(directory))
                .anyMatch(path -> path.toFile().isFile());
        } catch (IOException e) {
            return false;
        }
    }

    private String getCommitMessage(Project project, ShipkitConfiguration lenientConf) {
        String commitMessage = lenientConf.getJavadoc().getCommitMessage();
        if (isNull(commitMessage)) {
            commitMessage = "Update current and ${version} Javadocs.";
        }
        commitMessage = commitMessage.replaceAll("\\$\\{version\\}", project.getVersion().toString());
        return commitMessage;
    }

    private void deleteBuildDIrInRootProjectWhenCleanTask(Project project) {
        Set<Task> cleanTasks = project.getTasksByName("clean", true);
        cleanTasks.forEach(task -> task.doLast(t -> {
            project.delete(project.getBuildDir().getAbsolutePath());
        }));
    }

    private class CopyTasks {
        private final Set<Copy> copyToVersionTasks;
        private final Set<Copy> copyToCurrentTasks;

        private CopyTasks(Set<Copy> copyToVersionTasks, Set<Copy> copyToCurrentTasks) {
            this.copyToVersionTasks = copyToVersionTasks;
            this.copyToCurrentTasks = copyToCurrentTasks;
        }
    }
}
