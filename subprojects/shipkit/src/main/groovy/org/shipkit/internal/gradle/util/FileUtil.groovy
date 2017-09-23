package org.shipkit.internal.gradle.util

import groovy.transform.CompileStatic
import org.shipkit.internal.notes.header.HeaderRemover

/**
 * File utilities.
 */
@CompileStatic
class FileUtil {

    //TODO (maybe) convert to Java at some point

    /**
     * Appends content to the top of the file.
     */
    static void appendToTop(String content, File notesFile) {
        notesFile.getParentFile().mkdirs()
        notesFile.createNewFile()
        def existing = notesFile.text
        def existingWithoutHeader = HeaderRemover.removeHeaderIfExist(existing)

        notesFile.text = content + existingWithoutHeader
    }

    /**
     * Finds all files matching {@param pattern} in {@param rootDir} and below it.
     * @param pattern in Ant format @see <a href="https://ant.apache.org/manual/dirtasks.html#patterns">https://ant.apache.org/manual/dirtasks.html#patterns</a>
     * eg. ["**.log", "**\/**.txt"]
     * @param rootDir - absolute path to root for search
     */
    static List<String> findFilesByPattern(String rootDir, String pattern){
        FileNameFinder fnf = new FileNameFinder();
        return fnf.getFileNames(rootDir, pattern);
    }
}
