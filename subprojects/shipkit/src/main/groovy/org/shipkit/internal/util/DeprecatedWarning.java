package org.shipkit.internal.util;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

public class DeprecatedWarning {

    private final static Logger LOG = Logging.getLogger(DeprecatedWarning.class);

    public static void warn(String taskName) {
        LOG.lifecycle("  [DEPRECATED] {} is deprecated and may be removed in a new major release.", taskName);
    }
}
