package org.shipkit.internal.comparison.diff;

public class Diff {

    private final String previousFilePath;
    private final String currentFilePath;
    private final boolean filesEqual;
    private final String diffOutput;

    private Diff(String previousFilePath, String currentFilePath, boolean filesEqual, String diffOutput) {
        this.previousFilePath = previousFilePath;
        this.currentFilePath = currentFilePath;
        this.filesEqual = filesEqual;
        this.diffOutput = diffOutput;
    }

    public static Diff ofEqualFiles(String previousFile, String currentFile) {
        return new Diff(previousFile, currentFile, true, "");
    }

    public static Diff ofDifferentFiles(String previousFile, String currentFile, String diffOutput) {
        return new Diff(previousFile, currentFile, false, diffOutput);
    }

    public String getPreviousFilePath() {
        return previousFilePath;
    }

    public String getCurrentFilePath() {
        return currentFilePath;
    }

    public boolean areFilesEqual() {
        return filesEqual;
    }

    public String getDiffOutput() {
        return diffOutput;
    }
}
