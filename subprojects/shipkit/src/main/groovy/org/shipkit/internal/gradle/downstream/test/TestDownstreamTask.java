package org.shipkit.internal.gradle.downstream.test;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.internal.exec.SilentExecTask;
import org.shipkit.internal.gradle.git.CloneGitRepositoryTaskFactory;
import org.shipkit.internal.gradle.git.tasks.CloneGitRepositoryTask;
import org.shipkit.internal.gradle.release.tasks.UploadGistsTask;
import org.shipkit.internal.gradle.util.GradleWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.shipkit.internal.gradle.util.StringUtil.capitalize;
import static org.shipkit.internal.util.RepositoryNameUtil.extractRepoNameFromGitHubUrl;
import static org.shipkit.internal.util.RepositoryNameUtil.repositoryNameToCamelCase;

/**
 * Aggregates all downstream-test-related tasks. It can be configured to run e2e tests on provided repositories.
 * E2E test for each downstream repository is divided into a few stages:
 *  - clone downstream project to '$buildDir/downstream'
 *  - execute 'testRelease' task using the newest Shipkit version inside the downstream clone
 *  - saves output logs to file or creates gists for them (see {@link UploadGistsTask})
 *
 * Adds tasks:
 * <ul>
 *     <li>clone$projectName - {@link CloneGitRepositoryTask}</li>
 *     <li>test$projectName - {@link SilentExecTask}</li>
 * </ul>
 */
public class TestDownstreamTask extends DefaultTask {

    private static final Logger LOG = Logging.getLogger(TestDownstreamTask.class);

    private List<String> repositories = new ArrayList<>();
    private File logsDirectory;
    private UploadGistsTask uploadGistsTask;
    private String gitHubUrl;

    /**
     * Creates an e2e test, for @param #repositoryUrl of downstream service, that should be executed
     * whenever upstream service (the one where {@link TestDownstreamPlugin} is applied) is released.
     */
    public void addRepository(String repositoryUrl) {
        LOG.debug("Downstream test created for repository {}", repositoryUrl);
        repositories.add(repositoryUrl);
        createTasks(repositoryUrl);
    }

    private void createTasks(String gitHubRepoUrl) {
        String repoName = extractRepoNameFromGitHubUrl(gitHubRepoUrl);
        String camelCaseRepoName = repositoryNameToCamelCase(repoName);
        CloneGitRepositoryTask clone = CloneGitRepositoryTaskFactory.createCloneTask(getProject(), gitHubUrl, repoName);
        createRunTestReleaseTask(camelCaseRepoName, clone);
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
        run.setCommand(asList(GradleWrapper.getWrapperCommand(),
                "releaseNeeded", "performRelease",
                "releaseCleanUp", "-PdryRun",
                "-x", "gitPush", "-x", "bintrayUpload", "-x", "updateReleaseNotesOnGitHub",
                "-x", "pushJavadoc",
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

    /**
     * See {@link org.shipkit.gradle.configuration.ShipkitConfiguration.GitHub#getUrl()}
     */
    public String getGitHubUrl() {
        return gitHubUrl;
    }

    /**
     * See {@link org.shipkit.gradle.configuration.ShipkitConfiguration.GitHub#getUrl()}
     */
    public void setGitHubUrl(String gitHubUrl) {
        this.gitHubUrl = gitHubUrl;
    }
}
