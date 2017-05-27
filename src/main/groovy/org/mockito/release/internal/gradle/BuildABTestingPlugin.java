package org.mockito.release.internal.gradle;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.CopySpec;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.*;
import org.json.simple.Jsonable;
import org.json.simple.Jsoner;
import org.mockito.release.exec.DefaultProcessRunner;
import org.mockito.release.internal.comparison.file.FileDifferenceProvider;
import org.mockito.release.notes.util.IOUtil;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

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
     * A Task which compares two given directories. The result will be serialized to a result file.
     */
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

        @TaskAction
        public void compareDirectories() {
            if (resultsFile.exists()) {
                getProject().delete(resultsFile);
            }

            CompareResult compareResult = new FileDifferenceProvider().getDifference(dirA, dirB);
            IOUtil.writeFile(resultsFile, new CompareResultSerializer().serialize(compareResult));
        }
    }

    public static class CompareResult implements Jsonable {

        private static final String JSON_FORMAT = "{ \"onlyA\": \"%s\", \"onlyB\": \"%s\", " +
                "\"both\": \"%s\" }";

        private List<File> onlyA;
        private List<File> onlyB;
        private List<File> bothButDifferent;

        public void setOnlyA(List<File> file) {
            this.onlyA = file;
        }

        public void setOnlyB(List<File> file) {
            this.onlyB = file;
        }

        public void setBothButDifferent(List<File> file) {
            this.bothButDifferent = file;
        }

        @Override
        public String toJson() {
            return String.format(JSON_FORMAT,
                    Jsoner.serialize(toStringList(onlyA)),
                    Jsoner.serialize(toStringList(onlyB)),
                    Jsoner.serialize(toStringList(bothButDifferent)));
        }

        private List<String> toStringList(List<File> files) {
            List<String> ret = new ArrayList<String>(files.size());
            for (File file : files) {
                ret.add(file.getPath());
            }
            return ret;
        }

        @Override
        public void toJson(Writer writable) throws IOException {
            writable.append(toJson());
        }
    }

    public static class CompareResultSerializer {

        private static final Logger LOG = Logging.getLogger(CompareResultSerializer.class);

        public String serialize(CompareResult compareResult) {
            String json = Jsoner.serialize(compareResult);
            LOG.info("Serialize compare result to: {}", json);
            return json;
        }

        public CompareResult deserialize(String json) {
            return null;
        }

    }

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
