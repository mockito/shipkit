package org.mockito.release.internal.gradle.configuration;

import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

/**
 * Validates the inputs provided by users.
 */
public class BasicValidator {

    private final static Logger LOGGER = Logging.getLogger(BasicValidator.class);

    //TODO unit test

    /**
     * Throws {@link GradleException} with specified message if the object is null.
     * Returns the object if it is not not null.
     *
     * @param object the value to check for not-null
     * @param message the exception message emitted if object parameter is null
     */
    public static <T> T notNull(T object, String message) {
        if (object == null) {
            throw new GradleException(message);
        }
        return object;
    }

    /**
     * Similar to {@link #notNull(Object, String)}
     * but checks the env variable as fallback if object is null.
     * If object is null and the env variable is null or empty
     * the {@link GradleException} with specified message is thrown
     *
     * @param object the value to check for not-null
     * @param envVariable the name of env variable to inspect as fallback if object is null
     * @param message the exception message if object and env variable value are null
     */
    public static String notNull(String object, String envVariable, String message) {
        if (object != null) {
            return object;
        }

        String envValue = System.getenv(envVariable);
        if (envValue != null && !envValue.trim().isEmpty()) {
            LOGGER.info("Environment variable '" + envVariable + "' is found and will be used.");
            return envValue;
        } else {
            LOGGER.info("Environment variable '" + envVariable + "' was not found.");
        }

        throw new GradleException(message);
    }
}
