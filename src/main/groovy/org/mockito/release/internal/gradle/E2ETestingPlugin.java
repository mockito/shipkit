package org.mockito.release.internal.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.*;

import java.io.File;
import java.util.Collection;

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
            CloneGitHubRepositoryTask clone = project.getTasks().create("cloneProject" + repoName, CloneGitHubRepositoryTask.class);
            clone.setRepository(gitHubRepoUrl);
            clone.setTargetDir(new File(project.getBuildDir(), repoName + "-pristine"));

            //TODO MS git clone from -pristine instead of copy?
            File workDir = new File(project.getBuildDir(), repoName + "-work");
            Copy copy = project.getTasks().create("copyProject" + repoName, Copy.class);
            copy.dependsOn(clone);
            copy.from(clone.getTargetDir());
            copy.into(workDir);

            RunTestTask run = project.getTasks().create("runTest" + repoName, RunTestTask.class);
            run.dependsOn(copy);
            run.setWorkDir(workDir);
            //Using Gradle's composite builds ("--include-build") so that we're picking up current version of tools
            run.setCommand(asList("./gradlew", "publishToMavenLocal", "testRelease",
                    "-x", "gitPush", "-x", "bintrayUpload",
                    "--include-build", project.getRootDir().getAbsolutePath()));

            //we should put the build log in separate file instead of including it in the console of the parent build
            //otherwise the output will be really messy
            run.setBuildOutputFile(new File(project.getBuildDir(), repoName + "-build.log"));
        }

        // TODO MS Unit testing
        private String extractRepoName(String gitHubRepo) {
            return gitHubRepo.substring(gitHubRepo.lastIndexOf('/') + 1, gitHubRepo.length());
        }
    }

    public static class CloneGitHubRepositoryTask extends Exec {

        @Input private String repository;
        @OutputDirectory private File targetDir;

        @TaskAction
        public void cloneRepository() {
            LOG.lifecycle("  CloneGitHubRepositoryTask cloneRepository");
            workingDir("$projectDir");
            commandLine("git", "clone", repository, targetDir);
            exec();
        }

        public void setRepository(String repository) {
            this.repository = repository;
        }

        public void setTargetDir(File targetDir) {
            this.targetDir = targetDir;
        }

        public File getTargetDir() {
            return targetDir;
        }
    }


    public static class RunTestTask extends DefaultTask {

        private Collection<String> command;

        public void setWorkDir(File workDir) {

        }

        @Input
        public void setCommand(Collection<String> command) {
            this.command = command;
        }

        public void setBuildOutputFile(File file) {

        }
    }
}
