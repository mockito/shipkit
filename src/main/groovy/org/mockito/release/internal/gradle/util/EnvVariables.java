package org.mockito.release.internal.gradle.util;

public class EnvVariables {

    /**
     * Provides env variables and validates presence.
     *
     * TODO merge somehow with ExtContainer, have one class that has all settings.
     */
    public static String getEnv(String envName) {
        String value = System.getenv(envName);
        if (value == null) {
            throw new RuntimeException("Export '" + envName + "' env variable first!");
        }
        return value;
    }
}