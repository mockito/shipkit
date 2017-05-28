package org.mockito.release.internal.comparison.file;

import org.json.simple.Jsonable;
import org.json.simple.Jsoner;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the compare result of two directories. The result consists of files which are only available in dirA, files
 * which are only available in dirB and files which are available in both directories and their content is different.
 */
public class CompareResult implements Jsonable {

    private static final String JSON_FORMAT = "{ \"onlyA\": %s, \"onlyB\": %s, " +
            "\"both\": %s }";

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
