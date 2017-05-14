package org.mockito.release.internal.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.*;
import org.mockito.release.exec.*;

import java.io.File;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * This plugin tests your library end-to-end (e2e) using client projects.
 * TODO MS doc
 */
public class E2ETestingPlugin implements Plugin<Project> {

    private static final Logger LOG = Logging.getLogger(ContinuousDeliveryPlugin.class);

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
            CloneGitHubRepositoryTask clone = project.getTasks().create("cloneProjectFromGitHub" + repoName, CloneGitHubRepositoryTask.class);
            clone.setRepository(gitHubRepoUrl);
            clone.setTargetDir(new File(project.getBuildDir(), repoName + "-pristine"));
            // For now for easier testing
            clone.dependsOn("clean");

            // Clone from *-pristine to *-work. Copy task will not work because of ignoring git specific files:
            // https://discuss.gradle.org/t/copy-git-specific-files/11970
            File workDir = new File(project.getBuildDir(), repoName + "-work");
            CloneGitHubRepositoryTask copy = project.getTasks().create("cloneProjectToWorkDir" + repoName, CloneGitHubRepositoryTask.class);
            copy.dependsOn(clone);
            copy.setRepository(clone.getTargetDir().getAbsolutePath());
            copy.setTargetDir(workDir);

            RunTestTask run = project.getTasks().create("runTest" + repoName, RunTestTask.class);
            run.dependsOn(copy);
            run.setWorkDir(workDir);
            run.setRepoName(repoName);

            //Using Gradle's composite builds ("--include-build") so that we're picking up current version of tools
            run.setCommand(asList("./gradlew", "publishToMavenLocal", "testRelease",
                    "-x", "gitPush", "-x", "bintrayUpload",
                    "--include-build", project.getRootDir().getAbsolutePath(), "-s"));

            //we should put the build log in separate file instead of including it in the console of the parent build
            //otherwise the output will be really messy
            run.setBuildOutputFile(new File(project.getBuildDir(), repoName + "-build.log"));
        }

        // TODO MS Unit testing
        private String extractRepoName(String gitHubRepo) {
            return gitHubRepo.substring(gitHubRepo.lastIndexOf('/') + 1, gitHubRepo.length());
        }
    }

    public static class CloneGitHubRepositoryTask extends DefaultTask {

        private String repository;
        private File targetDir;

        @TaskAction
        public void cloneRepository() {
            LOG.lifecycle("  Clone repository");
            getProject().getBuildDir().mkdirs();    // build dir can be not created yet
            ProcessRunner processRunner = org.mockito.release.exec.Exec.getProcessRunner(getProject().getBuildDir());
            processRunner.run("git", "clone", repository, targetDir.getAbsolutePath());
        }

        @Input
        public void setRepository(String repository) {
            this.repository = repository;
        }

        @OutputDirectory
        public void setTargetDir(File targetDir) {
            this.targetDir = targetDir;
        }

        public File getTargetDir() {
            return targetDir;
        }
    }


    public static class RunTestTask extends DefaultTask {

        private List<String> command;
        private File buildOutput;
        private File workDir;
        private String repoName;

        @TaskAction
        public void runTest() {
            LOG.lifecycle("  Run test of {}. The output will be save in {}", repoName, buildOutput.getAbsoluteFile());
            ProcessRunner processRunner = org.mockito.release.exec.Exec.getProcessRunner(workDir, buildOutput);
            processRunner.run(command);
        }

        @Input
        public void setWorkDir(File workDir) {
            this.workDir = workDir;
        }

        @Input
        public void setCommand(List<String> command) {
            this.command = command;
        }

        @OutputFile
        public void setBuildOutputFile(File file) {
            buildOutput = file;
        }

        @Input
        public void setRepoName(String repoName) {
            this.repoName = repoName;
        }

    }
}
