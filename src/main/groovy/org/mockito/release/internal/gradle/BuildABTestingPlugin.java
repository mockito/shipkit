package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.CopySpec;
import org.gradle.api.tasks.*;
import org.mockito.release.exec.DefaultProcessRunner;

import java.io.File;

/**
 * See rationale and design at: https://github.com/mockito/mockito-release-tools/issues/113
 */
public class BuildABTestingPlugin implements Plugin<Project> {

    public void apply(final Project project) {
        //Step 1. Clone the repo for A/B testing
        CloneGitRepositoryTask clone = project.getTasks().create("cloneGitRepository", CloneGitRepositoryTask.class);
        clone.setRepository("https://github.com/mockito/mockito");
        clone.setTargetDir(new File(project.getBuildDir(), "abTesting/mockito/pristine"));

        //Step 2. Run A test
        RunTestTask runA = project.getTasks().create("runA", RunTestTask.class);
        runA.dependsOn(clone);
        runA.setSourceDir(clone.getTargetDir());
        runA.setWorkDir(new File(project.getBuildDir(), "abTesting/mockito/testA-" + System.currentTimeMillis()));
        // using assemble task for now in order to get different results
        runA.commandLine("./gradlew", "assemble");
        runA.setOutputDir(new File(runA.getWorkDir(), "build"));

        //Step 3. Run B test
        RunTestTask runB = project.getTasks().create("runB", RunTestTask.class);
        runB.dependsOn(clone);
        runB.setSourceDir(clone.getTargetDir());
        runB.setWorkDir(new File(project.getBuildDir(), "abTesting/mockito/testB-" + System.currentTimeMillis()));
        runB.commandLine("./gradlew", "build");
        runB.setOutputDir(new File(runB.getWorkDir(), "build"));

        //Step 4. Compare test outcomes
        CompareDirectoriesTask compare = project.getTasks().create("compareAB", CompareDirectoriesTask.class);
        compare.dependsOn(runA, runB);
        compare.setDirA(runA.getOutputDir());
        compare.setDirB(runB.getOutputDir());
        compare.setResultsFile(new File(project.getBuildDir(), "abTesting/mockito/results.json")); //or JSON :)

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

        @TaskAction
        public void analyze() {

        }
    }

    /**
     * Gradle task to run a given command line.
     * Note: not yet migrated to RunTestReleaseTask because RunTestReleaseTask requires two separate checkout tasks
     * which might take some time (e.g. if we are using a repo like mockito and the internet connection is not that fast.
     */
    public static class RunTestTask extends DefaultTask {

        private File sourceDir;
        private File workDir;
        private File outputDir;
        private String[] arg;

        @InputDirectory
        public void setSourceDir(File sourceDir) {
            this.sourceDir = sourceDir;
        }

        public void setWorkDir(File workDir) {
            this.workDir = workDir;
        }

        @Input
        public void commandLine(String ... arg) {
            this.arg = arg;
        }

        public File getWorkDir() {
            return workDir;
        }

        public void setOutputDir(File outputDir) {
            this.outputDir = outputDir;
        }

        @OutputDirectory
        public File getOutputDir() {
            return outputDir;
        }

        @TaskAction
        public void executeTestTask() {
            if (sourceDir == null || !sourceDir.exists()) {
                throw new RuntimeException("Invalid source dir '" + sourceDir + "' given!" );
            }
            if (outputDir != null && !outputDir.exists()) {
                outputDir.mkdirs();
            }
            getProject().copy(new Action<CopySpec>() {
                public void execute(CopySpec copy) {
                    copy.from(sourceDir).into(workDir);
                }
            });
            new DefaultProcessRunner(workDir).run(arg);
        }
    }

    public static class CompareABTask extends DefaultTask {
    }

}
