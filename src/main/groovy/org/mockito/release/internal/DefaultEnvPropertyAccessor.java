package org.mockito.release.internal;

import org.mockito.release.internal.util.EnvPropertyAccessor;

public class DefaultEnvPropertyAccessor implements EnvPropertyAccessor{

    @Override
    public String getenv(String name) {
        return System.getenv(name);
    }
}
