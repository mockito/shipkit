package org.mockito.release.internal.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.mockito.release.internal.comparison.file.CompareResult;
import org.mockito.release.internal.comparison.file.CompareResultSerializer;
import org.mockito.release.internal.comparison.file.FileDifferenceProvider;
import org.mockito.release.notes.util.IOUtil;

import java.io.File;

/**
 * A Task which compares two given directories. The result will be serialized to a result file.
 */
public class CompareDirectoriesTask extends DefaultTask {

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
