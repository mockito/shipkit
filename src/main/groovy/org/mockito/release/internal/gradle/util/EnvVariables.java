package org.mockito.release.internal.gradle.util;

public class EnvVariables {

    /**
     * Provides env variables and validates presence.
     *
     * TODO merge somehow with ExtContainer, have one class that has all settings.
     * TODO validate presence of every required env variable that will be needed during the build before the build starts
     */
    public static String getEnv(String envName) {
        String value = System.getenv(envName);
        if (value == null) {
            //TODO we should prefix all exception emitted by mockito release tools
            //otherwise it's hard to figure out what is failing, whether it's Bintray's task that fails or something else
            throw new RuntimeException("Export '" + envName + "' env variable first!");
        }
        return value;
    }
}