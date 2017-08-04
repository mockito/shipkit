package org.shipkit.internal.gradle.util

import groovy.transform.CompileStatic
import org.shipkit.internal.notes.header.HeaderProvider
import org.shipkit.internal.notes.header.HeaderRemover

/**
 * File utilities.
 */
@CompileStatic
class FileUtil {

    //TODO (maybe) convert to Java at some point

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
        notesFile.getParentFile().mkdirs()
        notesFile.createNewFile()
        def existing = notesFile.text
        def existingWithoutHeader =  HeaderRemover.removeHeaderIfExist(existing)

        notesFile.text = content + existingWithoutHeader
    }

    private static String removeHeader(String existing) {
        return existing.replaceFirst(HeaderProvider.HEADER_START + ".*" + HeaderProvider.HEADER_END+"[\\r\\n]+", "")
    }
}
