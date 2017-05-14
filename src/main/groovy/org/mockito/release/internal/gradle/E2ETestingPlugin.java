package org.mockito.release.internal.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;

import static java.util.Arrays.asList;
import static org.mockito.release.internal.gradle.util.StringUtil.capitalize;

/**
 * This plugin tests your library end-to-end (e2e) using client projects.
 * Plugin clones client projects to '$buildDir/project-name-pristine' first, next clone project from 'pristine' to
 * '$buildDir/project-name-work' and execute 'testRelease' task using the newest mockito-release-tools version
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
        E2ETest e2eTest = project.getExtensions().create("e2eTest", E2ETest.class, project);
        // TODO hardcoded for now
        e2eTest.create("https://github.com/mockito/mockito-release-tools-example");
    }

    public static class E2ETest {

        Project project;

        public E2ETest(Project project) {
            this.project = project;
        }

        void create(String gitHubRepoUrl) {
            String repoName = extractRepoName(gitHubRepoUrl);
            // TODO add depth clone configuration for shallow clone
            CloneGitRepositoryTask clone = project.getTasks().create(
                    "cloneProjectFromGitHub" + capitalize(repoName),
                    CloneGitRepositoryTask.class);
            clone.setRepository(gitHubRepoUrl);
            clone.setTargetDir(new File(project.getBuildDir(), repoName + "-pristine"));
            // For now for easier testing
            clone.dependsOn("clean");

            // Clone from *-pristine to *-work. Copy task will not work because of ignoring git specific files:
            // https://discuss.gradle.org/t/copy-git-specific-files/11970
            // Furthermore we can verify push to pristine origin
            File workDir = new File(project.getBuildDir(), repoName + "-work");
            CloneGitRepositoryTask copy = project.getTasks().create(
                    "cloneProjectToWorkDir" + capitalize(repoName),
                    CloneGitRepositoryTask.class);
            copy.dependsOn(clone);
            copy.setRepository(clone.getTargetDir().getAbsolutePath());
            copy.setTargetDir(workDir);

            RunTestReleaseTask run = project.getTasks().create(
                    "runTest" + capitalize(repoName),
                    RunTestReleaseTask.class);
            run.dependsOn(copy);
            run.setWorkDir(workDir);
            run.setRepoName(repoName);

            // Using Gradle's composite builds ("--include-build") so that we're picking up current version of tools
            run.setCommand(asList("./gradlew", "publishToMavenLocal", "testRelease",
                    "-x", "gitPush", "-x", "bintrayUpload",
                    "--include-build", project.getRootDir().getAbsolutePath(), "-s"));

            // Build log in separate file instead of including it in the console of the parent build
            // Otherwise the output will be really messy
            run.setBuildOutputFile(new File(project.getBuildDir(), repoName + "-build.log"));
        }

        // TODO MS Unit testing
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
