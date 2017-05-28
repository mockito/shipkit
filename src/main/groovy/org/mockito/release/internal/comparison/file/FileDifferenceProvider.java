package org.mockito.release.internal.comparison.file;

import org.mockito.release.internal.util.Md5;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FileDifferenceProvider {

    public CompareResult getDifference(File dirA, File dirB) {
        List<File> dirAFiles = getFilesResursive(dirA.listFiles());
        Collections.sort(dirAFiles);
        List<File> dirBFiles = getFilesResursive(dirB.listFiles());
        Collections.sort(dirBFiles);

        List<File> onlyA = new ArrayList<File>();
        List<File> onlyB = new ArrayList<File>();
        List<File> bothButDifferent = new ArrayList<File>();

        int i = 0;
        int j = 0;

        while (dirAFiles.size() > i && dirBFiles.size() > j) {
            String dirASubPath = dirAFiles.get(i).getPath().substring(dirA.getPath().length());
            String dirBSubPath = dirBFiles.get(j).getPath().substring(dirB.getPath().length());
            int compareResult = dirASubPath.compareTo(dirBSubPath);

            if (compareResult < 0) {
                onlyA.add(dirAFiles.get(i));
                i++;
            } else if (compareResult > 0) {
                onlyB.add(dirBFiles.get(j));
                j++;
            } else {
                String md5A = Md5.calculate(dirAFiles.get(i));
                String md5B = Md5.calculate(dirBFiles.get(j));
                boolean sameMd5 = md5A.equals(md5B);

                if (dirAFiles.get(i).length() == dirBFiles.get(j).length() && sameMd5) {
                    // nothing to do, both files are available
                } else {
                    bothButDifferent.add(dirAFiles.get(i));
                    bothButDifferent.add(dirBFiles.get(j));
                }
                i++;
                j++;
            }
        }

        if (dirAFiles.size() == i && dirBFiles.size() > j) {
            while (j < dirBFiles.size()) {
                onlyB.add(dirBFiles.get(j));
                j++;
            }
        }

        if (dirBFiles.size() == j && dirAFiles.size() > i) {
            while (i < dirAFiles.size()) {
                onlyA.add(dirAFiles.get(i));
                i++;
            }
        }

        CompareResult result = new CompareResult();
        result.setOnlyA(onlyA);
        result.setOnlyB(onlyB);
        result.setBothButDifferent(bothButDifferent);

        return result;
    }


    private List<File> getFilesResursive(File[] files) {
        List<File> filesRecursive = new ArrayList<File>();
        for (File file : files) {
            filesRecursive.add(file);
            if (file.isDirectory()) {
                filesRecursive.addAll(getFilesResursive(file.listFiles()));
            }
        }
        return filesRecursive;
    }
}
