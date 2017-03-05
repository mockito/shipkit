package org.mockito.release.internal.gradle

import groovy.transform.CompileStatic

/**
 * File utilities.
 */
@CompileStatic
class FileUtil {

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
}
