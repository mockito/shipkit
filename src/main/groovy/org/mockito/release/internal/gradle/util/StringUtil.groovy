package org.mockito.release.internal.gradle.util

import groovy.transform.CompileStatic

/**
 * String utilities.
 */
@CompileStatic
class StringUtil {

    //TODO convert to Java at some point

    /**
     * Classic string join
     */
    static String join(Collection<String> collection, String separator) {
        return collection.join(separator)
    }

    /**
     * Capitalizes string
     */
    static String capitalize(String input) {
        return input.capitalize();
    }

    /**
     * Checks if input is empty
     */
    static boolean isEmpty(String input) {
        return input == null || input.isEmpty();
    }
}
