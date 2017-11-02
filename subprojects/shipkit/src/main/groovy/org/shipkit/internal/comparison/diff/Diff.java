package org.shipkit.internal.comparison.diff;

public class Diff {

    private final boolean filesEqual;
    private final String diffOutput;

    private Diff(boolean filesEqual, String diffOutput) {
        this.filesEqual = filesEqual;
        this.diffOutput = diffOutput;
    }

    public static Diff ofEqualFiles() {
        return new Diff(true, "");
    }

    public static Diff ofDifferentFiles(String diffOutput) {
        return new Diff(false, diffOutput);
    }

    public boolean areFilesEqual() {
        return filesEqual;
    }

    public String getDiffOutput() {
        return diffOutput;
    }
}
