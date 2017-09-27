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
}
