package org.shipkit.internal.gradle.configuration;

import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.internal.gradle.util.StringUtil;

/**
 * Validates the inputs provided by users.
 */
public class BasicValidator {

    private final static Logger LOGGER = Logging.getLogger(BasicValidator.class);

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

        return notNullEnv(envVariable, System.getenv(envVariable), message);
    }

    static String notNullEnv(String envVarName, String envVarValue, String message) {
        if (StringUtil.isEmpty(envVarValue)) {
            LOGGER.info("Environment variable '" + envVarName + "' is found and will be used.");
            return envVarValue;
        } else {
            LOGGER.info("Environment variable '" + envVarName + "' was not found.");
        }

        throw new GradleException(message);
    }
}
