package org.mockito.release.internal.gradle.configuration;

import org.gradle.api.GradleException;

/**
 * Validates the inputs provided by users.
 * Use it to validate settings that should be configured in the build.gradle by the user.
 * For settings that are relevant only to release builds (like GitHub and Bintray secret keys) use {@link LazyValidator} instead.
 */
public class BasicValidator {

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
