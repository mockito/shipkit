package org.mockito.release.internal.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.*;
import org.mockito.release.exec.DefaultProcessRunner;

import java.io.File;

/**
 * See rationale and design at: https://github.com/mockito/mockito-release-tools/issues/113
 */
public class BuildABTestingPlugin implements Plugin<Project> {

    public void apply(final Project project) {
        //Step 1. Clone the repo for A/B testing
        CloneGitHubRepositoryTask clone = project.getTasks().create("cloneGitHubRepository", CloneGitHubRepositoryTask.class);
        clone.setRepository("mockito/mockito");
        clone.setTargetDir(new File(project.getBuildDir(), "abTesting/mockito/pristine"));

        //Step 2. Run A test
        RunTestTask runA = project.getTasks().create("runA", RunTestTask.class);
        runA.dependsOn(clone);
        runA.setSourceDir(clone.getTargetDir());
        runA.setWorkDir(new File(project.getBuildDir(), "abTesting/mockito/testA-" + System.currentTimeMillis()));
        runA.commandLine("./gradlew", "build");
        runA.setOutputDir(new File(runA.getWorkDir(), "build"));

        //Step 3. Run B test
        RunTestTask runB = project.getTasks().create("runB", RunTestTask.class);
        runA.dependsOn(clone);
        runA.setSourceDir(clone.getTargetDir());
        runA.setWorkDir(new File(project.getBuildDir(), "abTesting/mockito/testB-" + System.currentTimeMillis()));
        runA.commandLine("./gradlew", "build");
        runA.setOutputDir(new File(runA.getWorkDir(), "build"));

        //Step 4. Compare test outcomes
        CompareDirectoriesTask compare = project.getTasks().create("compareAB", CompareDirectoriesTask.class);
        compare.dependsOn(runA, runB);
        compare.setDirA(runA.getOutputDir());
        compare.setDirB(runB.getOutputDir());
        compare.setResultsFile(new File(project.getBuildDir(), "abTesting/mockito/results.ser")); //or JSON :)

        //Step 5. Analyze comparison results
        AnalyzeComparisonResultsTask analyze = project.getTasks().create("analyzeAB", AnalyzeComparisonResultsTask.class);
        analyze.dependsOn(compare);
        analyze.setComparisonResultsFile(compare.getResultsFile());
    }

    /**
     * BELOW task types are only empty shells.
     * They act as suggestions for workflow / API design.
     */

    public static class AnalyzeComparisonResultsTask extends DefaultTask {

        private File comparisonResultsFile;

        @InputFile
        public void setComparisonResultsFile(File comparisonResultsFile) {
            this.comparisonResultsFile = comparisonResultsFile;
        }
    }

    public static class CompareDirectoriesTask extends DefaultTask {

        private File dirA;
        private File dirB;
        private File resultsFile;

        @InputDirectory
        public void setDirA(File dir) {
            this.dirA = dir;
        }

        @InputDirectory
        public void setDirB(File dir) {
            this.dirB = dir;
        }

        public void setResultsFile(File file) {
            this.resultsFile = file;
        }

        @OutputFile
        public File getResultsFile() {
            return resultsFile;
        }
    }

    public static class RunTestTask extends DefaultTask {

        private File sourceDir;
        private File workDir;
        private File outputDir;

        @InputDirectory
        public void setSourceDir(File sourceDir) {
            this.sourceDir = sourceDir;
        }

        public void setWorkDir(File workDir) {
            this.workDir = workDir;
        }

        @Input
        public void commandLine(String ... arg) {
        }

        public File getWorkDir() {
            return workDir;
        }

        @OutputDirectory
        public void setOutputDir(File outputDir) {
            this.outputDir = outputDir;
        }

        public File getOutputDir() {
            return outputDir;
        }
    }

    public static class CompareABTask extends DefaultTask {
    }

    public static class CloneGitHubRepositoryTask extends DefaultTask {

        private String repository;
        private File targetDir;
        private static final String REPO_BASE_URL = "https://github.com/";

        @Input
        public void setRepository(String repository) {
            this.repository = repository;
        }

        public void setTargetDir(File targetDir) {
            this.targetDir = targetDir;
        }

        @OutputDirectory
        public File getTargetDir() {
            return targetDir;
        }

        @TaskAction
        public void cloneGit() {
            if(!targetDir.exists()) {
                targetDir.mkdirs();
            }

            if (repository == null || repository.isEmpty()) {
                throw new RuntimeException("Invalid repository '" + repository + "' given!");
            }

            String url = REPO_BASE_URL + repository;

            new DefaultProcessRunner(targetDir).run("git", "clone", url);
        }
    }

    public static class PrepareABTestingTask extends DefaultTask {

        private File sourceDir;

        @InputDirectory
        public void setSourceDir(File sourceDir) {
            this.sourceDir = sourceDir;
        }
    }
}
