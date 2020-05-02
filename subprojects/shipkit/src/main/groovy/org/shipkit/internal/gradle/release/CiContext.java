package org.shipkit.internal.gradle.release;

import org.shipkit.internal.util.EnvVariables;

public class CiContext {

    private final EnvVariables envVariables;

    public CiContext() {
        this.envVariables = new EnvVariables();
    }

    CiContext(EnvVariables envVariables) {
        this.envVariables = envVariables;
    }

    /**
     * Returns true if it's a build inside CI environment, false otherwise
     */
    public boolean isCiBuild() {
        String ci = envVariables.getNonEmptyEnv("CI"); // CI env variable is set by CI
        return "true".equals(ci);
    }
}
