package org.shipkit.internal.util;

import org.shipkit.internal.gradle.util.StringUtil;

/**
 * Utility wrapper for {@link System#getenv(String)}.
 * Mostly for testing mocking, but also hosts one extra useful method.
 */
public class EnvVariables {

    public String getenv(String name) {
        return System.getenv(name);
    }

    /**
     * Returns env variable or null if the env variable is empty
     */
    public String getNonEmptyEnv(String name) {
        String value = System.getenv(name);
        return getNonEmpty(value);
    }

    static String getNonEmpty(String value) {
        if (StringUtil.isEmpty(value)) {
            return null;
        }
        return value;
    }
}
