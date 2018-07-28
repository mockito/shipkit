package org.shipkit.internal.gradle.util;

import java.util.Locale;

/**
 * Utilities for Gradle Wrapper
 */
public class GradleWrapper {

    private final static boolean WINDOWS = System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win");

    /**
     * Gets Gradle wrapper command safely for Linux ("./gradlew") and for Windows ("gradlew.bat")
     */
    public static String getWrapperCommand() {
        return WINDOWS ? "gradlew.bat" : "./gradlew";
    }
}
