package org.shipkit.internal.comparison.diff;

import java.io.File;

public class Diff {

    private final File previousFile;
    private final File currentFile;
    private final boolean filesEqual;
    private final String diffOutput;

    private Diff(File previousFile, File currentFile, boolean filesEqual, String diffOutput) {
        this.previousFile = previousFile;
        this.currentFile = currentFile;
        this.filesEqual = filesEqual;
        this.diffOutput = diffOutput;
    }

    public static Diff ofEqualFiles(File previousFile, File currentFile){
        return new Diff(previousFile, currentFile, true, "");
    }

    public static Diff ofDifferentFiles(File previousFile, File currentFile, String diffOutput){
        return new Diff(previousFile, currentFile, false, diffOutput);
    }

    public File getPreviousFile() {
        return previousFile;
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public boolean areFilesEqual() {
        return filesEqual;
    }

    public String getDiffOutput() {
        return diffOutput;
    }
}
