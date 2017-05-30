package org.shipkit.internal.util;

/**
 * wrapper for System.getenv, so that we can mock it easily in classes where we use it
 */
public class EnvVariables {
    public String getenv(String name) {
        return System.getenv(name);
    }
}
