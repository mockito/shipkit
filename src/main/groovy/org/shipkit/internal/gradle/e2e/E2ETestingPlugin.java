package org.shipkit.internal.gradle.e2e;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.shipkit.internal.gradle.git.CloneGitRepositoryTask;

import java.io.File;

import static java.util.Arrays.asList;
import static org.shipkit.internal.gradle.util.StringUtil.capitalize;

/**
 * This plugin tests your library end-to-end (e2e) using client projects.
 * Plugin clones client projects to '$buildDir/project-name-pristine' first, next clone project from 'pristine' to
 * '$buildDir/project-name-work' and execute 'testRelease' task using the newest shipkit version
 *
 * Adds tasks:
 * <ul>
 *     <li>cloneProjectFromGitHub$projectName - {@link CloneGitRepositoryTask}</li>
 *     <li>cloneProjectToWorkDir$projectName - {@link CloneGitRepositoryTask}</li>
 *     <li>runTest$projectName - {@link RunTestReleaseTask}</li>
 * </ul>
 */
public class E2ETestingPlugin implements Plugin<Project> {

    public void apply(final Project project) {
        project.getExtensions().create("e2eTest", E2ETest.class, project);
    }

    //TODO ms - closer to the finish line we need to make this type public in one of the public packages
    //this is how users will interface with configuring e2e tests
    public static class E2ETest {

        Project project;

        public E2ETest(Project project) {
            this.project = project;
        }

        void create(String gitHubRepoUrl) {
            String repoName = extractRepoName(gitHubRepoUrl);
            CloneGitRepositoryTask clone = createCloneProjectFromGitHub(gitHubRepoUrl, repoName);
            CloneGitRepositoryTask workDirCloneTask = createCloneProjectToWorkDirTask(repoName, clone);
            createRunTestReleaseTask(repoName, workDirCloneTask);
        }

        private CloneGitRepositoryTask createCloneProjectFromGitHub(String gitHubRepoUrl, String repoName) {
            CloneGitRepositoryTask clone = project.getTasks().create(
                    "cloneProjectFromGitHub" + capitalize(repoName),
                    CloneGitRepositoryTask.class);
            clone.setRepositoryUrl(gitHubRepoUrl);
            clone.setTargetDir(new File(project.getBuildDir(), repoName + "-pristine"));
            clone.setNumberOfCommitsToClone(50);
            // For now for easier testing
            clone.dependsOn("clean");
            return clone;
        }

        private CloneGitRepositoryTask createCloneProjectToWorkDirTask(String repoName, CloneGitRepositoryTask clone) {
            // Clone from *-pristine to *-work. Copy task will not work because of ignoring git specific files:
            // https://discuss.gradle.org/t/copy-git-specific-files/11970
            // Furthermore we can verify push to pristine origin
            File workDir = new File(project.getBuildDir(), repoName + "-work");
            CloneGitRepositoryTask copy = project.getTasks().create(
                    "cloneProjectToWorkDir" + capitalize(repoName),
                    CloneGitRepositoryTask.class);
            copy.dependsOn(clone);
            copy.setRepositoryUrl(clone.getTargetDir().getAbsolutePath());
            copy.setTargetDir(workDir);
            return copy;
        }

        private void createRunTestReleaseTask(String repoName, CloneGitRepositoryTask copy) {
            RunTestReleaseTask run = project.getTasks().create(
                    "runTestRelease" + capitalize(repoName),
                    RunTestReleaseTask.class);
            run.dependsOn(copy);
            run.setWorkDir(copy.getTargetDir());
            run.setRepoName(repoName);

            // Using Gradle's composite builds ("--include-build") so that we're picking up current version of tools
            run.setCommand(asList("./gradlew", "publishToMavenLocal", "testRelease",
                    "-x", "gitPush", "-x", "bintrayUpload",
                    "--include-build", project.getRootDir().getAbsolutePath(), "-s"));

            // Build log in separate file instead of including it in the console of the parent build
            // Otherwise the output will be really messy
            run.setBuildOutputFile(new File(project.getBuildDir(), repoName + "-build.log"));
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

}
