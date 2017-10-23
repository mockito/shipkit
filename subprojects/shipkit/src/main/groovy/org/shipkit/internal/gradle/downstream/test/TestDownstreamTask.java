package org.shipkit.internal.gradle.downstream.test;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.internal.exec.SilentExecTask;
import org.shipkit.internal.gradle.git.tasks.CloneGitRepositoryTask;
import org.shipkit.internal.gradle.release.tasks.UploadGistsTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.shipkit.internal.gradle.util.StringUtil.capitalize;
import static org.shipkit.internal.util.RepositoryNameUtil.*;

/**
 * Aggregates all downstream-test-related tasks. It can be configured to run e2e tests on provided repositories.
 * Automatically added tasks clone client projects to '$buildDir/project-name-pristine' first, next clone project from 'pristine' to
 * '$buildDir/project-name-work' and execute 'testRelease' task using the newest shipkit version
 *
 * Adds tasks:
 * <ul>
 *     <li>cloneProjectFromGitHub$projectName - {@link CloneGitRepositoryTask}</li>
 *     <li>cloneProjectToWorkDir$projectName - {@link CloneGitRepositoryTask}</li>
 *     <li>test$projectName - {@link SilentExecTask}</li>
 * </ul>
 */
public class TestDownstreamTask extends DefaultTask {

    private static final Logger LOG = Logging.getLogger(TestDownstreamTask.class);

    private List<String> repositories = new ArrayList<String>();
    private File logsDirectory;
    private UploadGistsTask uploadGistsTask;

    /**
     * URL of repository which will be downloaded and downstream test will be run on it
     */
    public void addRepository(String repositoryUrl) {
        LOG.debug("Downstream test created for repository {}", repositoryUrl);
        repositories.add(repositoryUrl);
        createTasks(repositoryUrl);
    }

    private void createTasks(String gitHubRepoUrl) {
        String repoName = extractRepoNameFromGitHubUrl(gitHubRepoUrl);
        String camelCaseRepoName = repositoryNameToCamelCase(repoName);
        CloneGitRepositoryTask clone = createCloneProjectFromGitHub(gitHubRepoUrl, camelCaseRepoName);
        CloneGitRepositoryTask workDirCloneTask = createCloneProjectToWorkDirTask(camelCaseRepoName, clone);
        createRunTestReleaseTask(camelCaseRepoName, workDirCloneTask);
    }

    private CloneGitRepositoryTask createCloneProjectFromGitHub(String gitHubRepoUrl, String camelCaseRepoName) {
        CloneGitRepositoryTask clone = getProject().getTasks().create(
                "cloneProjectFromGitHub" + capitalize(camelCaseRepoName),
                CloneGitRepositoryTask.class);
        clone.setRepositoryUrl(gitHubRepoUrl);
        clone.setTargetDir(new File(getProject().getBuildDir(), camelCaseRepoName + "Pristine"));
        clone.setDepth(50);
        // For now for easier testing
        clone.dependsOn("clean");
        return clone;
    }

    private CloneGitRepositoryTask createCloneProjectToWorkDirTask(String camelCaseRepoName, CloneGitRepositoryTask clone) {
        // Clone from *-pristine to *-work. Copy task will not work because of ignoring git specific files:
        // https://discuss.gradle.org/t/copy-git-specific-files/11970
        // Furthermore we can verify push to pristine origin
        File workDir = new File(getProject().getBuildDir(), camelCaseRepoName + "Work");
        CloneGitRepositoryTask copy = getProject().getTasks().create(
                "cloneProjectToWorkDir" + capitalize(camelCaseRepoName),
                CloneGitRepositoryTask.class);
        copy.dependsOn(clone);
        copy.setRepositoryUrl(clone.getTargetDir().getAbsolutePath());
        copy.setTargetDir(workDir);
        return copy;
    }

    private void createRunTestReleaseTask(final String camelCaseRepoName, CloneGitRepositoryTask copy) {
        final File buildOutputFile = new File(logsDirectory, camelCaseRepoName + "-build.log");
        SilentExecTask run = getProject().getTasks().create(
                "test" + capitalize(camelCaseRepoName),
                SilentExecTask.class);
        run.dependsOn(copy);
        run.finalizedBy(uploadGistsTask);
        run.setWorkDir(copy.getTargetDir());

        dependsOn(run);

        // Using Gradle's composite builds ("--include-build") so that we're picking up current version of tools
        run.setCommand(asList("./gradlew",
                "releaseNeeded", "performRelease",
                "releaseCleanUp", "-PdryRun",
                "-x", "gitPush", "-x", "bintrayUpload",
                "--include-build", getProject().getRootDir().getAbsolutePath(), "-s"));

        // Build log in separate file instead of including it in the console of the parent build
        // Otherwise the output will be really messy
        run.setBuildOutputFile(buildOutputFile);
        run.doFirst(new Action<Task>() {
            @Override
            public void execute(Task task) {
                LOG.lifecycle(testDownstreamLogMessage(camelCaseRepoName, buildOutputFile, uploadGistsTask.isEnabled(), uploadGistsTask.getName()));
            }
        });
    }

    static String testDownstreamLogMessage(String camelCaseRepoName, File buildOutputFile, boolean gistsUploadEnabled, String uploadGistsTaskName) {
        String prefix = "Run test of %s. ";
        if (gistsUploadEnabled) {
            return String.format(prefix +
                "The output will be uploaded to Gist, search for logs of '%s' task to see the access link.",
                camelCaseRepoName, uploadGistsTaskName);
        } else {
            return String.format(prefix + "The output will be saved in %s",
                camelCaseRepoName, buildOutputFile.getAbsoluteFile());
        }
    }

    /**
     * Directory where logs, containing output from test release tasks, are stored
     */
    public File getLogsDirectory() {
        return logsDirectory;
    }

    /**
     * See {@link #getLogsDirectory()}
     */
    public void setLogsDirectory(File logsDirectory) {
        this.logsDirectory = logsDirectory;
    }

    /**
     * UploadGistsTask that all test release tasks are finalized by
     */
    public UploadGistsTask getUploadGistsTask() {
        return uploadGistsTask;
    }

    /**
     * See {@link #getUploadGistsTask()}
     */
    public void setUploadGistsTask(UploadGistsTask uploadGistsTask) {
        this.uploadGistsTask = uploadGistsTask;
    }
}
