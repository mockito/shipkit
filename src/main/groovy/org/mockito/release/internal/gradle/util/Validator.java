package org.mockito.release.internal.gradle.util;

import org.gradle.api.GradleException;

/**
 * Validates the inputs provided by users
 */
public class Validator {

    /**
     * Throws {@link GradleException} with specified message if the object is null.
     * Returns the object if it is not not null.
     */
    public static <T> T notNull(T object, String message) {
        if (object == null) {
            throw new GradleException(message);
        }
        return object;
    }
}
