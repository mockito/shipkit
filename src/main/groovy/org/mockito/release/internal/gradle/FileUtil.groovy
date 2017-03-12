package org.mockito.release.internal.gradle

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

/**
 * File utilities.
 */
@CompileStatic
@PackageScope
class FileUtil {

    //TODO convert to Java at some point

    /**
     * Returns first line of the file.
     */
    static String firstLine(File notesFile) {
        return notesFile.withReader { it.readLine() }
    }

    /**
     * Appends content to the top of the file.
     */
    static void appendToTop(String content, File notesFile) {
        def existing = notesFile.text
        notesFile.text = content + existing
    }

    /**
     * Reads content of the file
     */
    static String readFile(File file) {
        file.text
    }

    /**
     * Writes content to the file
     */
    static void writeFile(File file, String content) {
        file.text = content
    }
}
