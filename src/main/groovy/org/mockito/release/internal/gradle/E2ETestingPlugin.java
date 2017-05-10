package org.mockito.release.internal.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.*;

import java.io.File;
import java.util.Collection;

import static java.util.Arrays.asList;

/**
 * See rationale and design at: https://github.com/mockito/mockito-release-tools/issues/85
 */
public class E2ETestingPlugin implements Plugin<Project> {

    /**
     * BELOW task types are only empty shells.
     * They act as suggestions for workflow / API design.
     */

    public void apply(final Project project) {
        project.getExtensions().create("e2eTest", E2ETest.class, project);
    }

    public static class E2ETest {

        Project project;

        public E2ETest(Project project) {
            this.project = project;
        }

        void create(String gitHubRepo) {
            //Very opinionated for now (e.g. hardcoded :), but we can make it configurable!
            CloneGitHubRepositoryTask clone = project.getTasks().create("cloneProject", CloneGitHubRepositoryTask.class);
            clone.setRepository(gitHubRepo);
            clone.setTargetDir(new File(project.getBuildDir(), gitHubRepo + "-pristine"));

            File workDir = new File(project.getBuildDir(), gitHubRepo + "-work");
            Copy copy = project.getTasks().create("copyProject", Copy.class);
            copy.dependsOn(clone);
            copy.from(clone.getTargetDir());
            copy.into(workDir);

            RunTestTask run = project.getTasks().create("runTest", RunTestTask.class);
            run.dependsOn(copy);
            run.setWorkDir(workDir);
            //Using Gradle's composite builds ("--include-build") so that we're picking up current version of tools
            run.setCommand(asList("./gradlew", "publishToMavenLocal", "testRelease",
                    "-x", "gitPush", "-x", "bintrayUpload",
                    "--include-build", project.getRootDir().getAbsolutePath()));

            //we should put the build log in separate file instead of including it in the console of the parent build
            //otherwise the output will be really messy
            run.setBuildOutputFile(new File(project.getBuildDir(), gitHubRepo + "-build.log"));
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

    public static class CloneGitHubRepositoryTask extends DefaultTask {

        @Input
        public void setRepository(String s) {
        }

        public void setTargetDir(File targetDir) {

        }

        @OutputDirectory
        public File getTargetDir() {
            return null;
        }
    }
}
