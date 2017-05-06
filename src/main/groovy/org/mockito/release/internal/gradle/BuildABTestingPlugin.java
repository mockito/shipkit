package org.mockito.release.internal.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.*;

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

        @InputFile
        public void setComparisonResultsFile(File comparisonResultsFile) {
        }
    }

    public static class CompareDirectoriesTask extends DefaultTask {

        @InputDirectory
        public void setDirA(File dir) {

        }

        @InputDirectory
        public void setDirB(File dir) {

        }

        public void setResultsFile(File file) {

        }

        @OutputFile
        public File getResultsFile() {
            return null;
        }
    }

    public static class RunTestTask extends DefaultTask {

        @InputDirectory
        public void setSourceDir(File sourceDir) {

        }

        public void setWorkDir(File workDir) {

        }

        @Input
        public void commandLine(String ... arg) {
        }

        public File getWorkDir() {
            return null;
        }

        @OutputDirectory
        public void setOutputDir(File outputDir) {

        }

        public File getOutputDir() {
            return null;
        }
    }

    public static class CompareABTask extends DefaultTask {
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

    public static class PrepareABTestingTask extends DefaultTask {

        @InputDirectory
        public void setSourceDir(File sourceDir) {

        }
    }
}
