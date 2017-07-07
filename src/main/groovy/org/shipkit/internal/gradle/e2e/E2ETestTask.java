package org.shipkit.internal.gradle.e2e;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.internal.gradle.git.CloneGitRepositoryTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.shipkit.internal.gradle.util.StringUtil.capitalize;

/**
 * Aggregates all e2e-related tasks. It can be configured to run e2e tests on provided repositories.
 * Automatically added tasks clone client projects to '$buildDir/project-name-pristine' first, next clone project from 'pristine' to
 * '$buildDir/project-name-work' and execute 'testRelease' task using the newest shipkit version
 *
 * Adds tasks:
 * <ul>
 *     <li>cloneProjectFromGitHub$projectName - {@link CloneGitRepositoryTask}</li>
 *     <li>cloneProjectToWorkDir$projectName - {@link CloneGitRepositoryTask}</li>
 *     <li>test$projectName - {@link RunTestReleaseTask}</li>
 * </ul>
 */
public class E2ETestTask extends DefaultTask {

    private static final Logger LOG = Logging.getLogger(E2ETestTask.class);

    private List<String> repositories = new ArrayList<String>();

    /**
     * URL of repository which will be downloaded and e2e test will be run on it
     */
    public void addRepository(String repositoryUrl) {
        LOG.debug("E2E test created for repository {}", repositoryUrl);
        repositories.add(repositoryUrl);
        createTasks(repositoryUrl);
    }

    private void createTasks(String gitHubRepoUrl) {
        String repoName = extractRepoName(gitHubRepoUrl);
        CloneGitRepositoryTask clone = createCloneProjectFromGitHub(gitHubRepoUrl, repoName);
        CloneGitRepositoryTask workDirCloneTask = createCloneProjectToWorkDirTask(repoName, clone);
        createRunTestReleaseTask(repoName, workDirCloneTask);
    }

    private CloneGitRepositoryTask createCloneProjectFromGitHub(String gitHubRepoUrl, String repoName) {
        CloneGitRepositoryTask clone = getProject().getTasks().create(
                "cloneProjectFromGitHub" + capitalize(repoName),
                CloneGitRepositoryTask.class);
        clone.setRepositoryUrl(gitHubRepoUrl);
        clone.setTargetDir(new File(getProject().getBuildDir(), repoName + "-pristine"));
        clone.setDepth(50);
        // For now for easier testing
        clone.dependsOn("clean");
        return clone;
    }

    private CloneGitRepositoryTask createCloneProjectToWorkDirTask(String repoName, CloneGitRepositoryTask clone) {
        // Clone from *-pristine to *-work. Copy task will not work because of ignoring git specific files:
        // https://discuss.gradle.org/t/copy-git-specific-files/11970
        // Furthermore we can verify push to pristine origin
        File workDir = new File(getProject().getBuildDir(), repoName + "-work");
        CloneGitRepositoryTask copy = getProject().getTasks().create(
                "cloneProjectToWorkDir" + capitalize(repoName),
                CloneGitRepositoryTask.class);
        copy.dependsOn(clone);
        copy.setRepositoryUrl(clone.getTargetDir().getAbsolutePath());
        copy.setTargetDir(workDir);
        return copy;
    }

    private void createRunTestReleaseTask(String repoName, CloneGitRepositoryTask copy) {
        RunTestReleaseTask run = getProject().getTasks().create(
                "test" + capitalize(repoName),
                RunTestReleaseTask.class);
        run.dependsOn(copy);
        run.setWorkDir(copy.getTargetDir());
        run.setRepoName(repoName);

        dependsOn(run);

        // Using Gradle's composite builds ("--include-build") so that we're picking up current version of tools
        run.setCommand(asList("./gradlew",
                "releaseNeeded", "performRelease",
                "releaseCleanUp", "-PdryRun",
                "-x", "gitPush", "-x", "bintrayUpload",
                "--include-build", getProject().getRootDir().getAbsolutePath(), "-s"));

        // Build log in separate file instead of including it in the console of the parent build
        // Otherwise the output will be really messy
        run.setBuildOutputFile(new File(getProject().getBuildDir(), repoName + "-build.log"));
    }

    private String extractRepoName(String gitHubRepo) {
        String text = gitHubRepo.trim();
        if(text.lastIndexOf('/') == text.length() - 1) {
            // cut last slash
            text = text.substring(0, text.length() - 1);
        }
        return text.substring(text.lastIndexOf('/') + 1, text.length());
    }
}
