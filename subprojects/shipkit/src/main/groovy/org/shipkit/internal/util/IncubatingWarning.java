package org.shipkit.internal.util;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

public class IncubatingWarning {

    private final static Logger LOG = Logging.getLogger(IncubatingWarning.class);

    public static void warn(String feature) {
        LOG.lifecycle("  [INCUBATING] {} is incubating and may change in any version.", feature);
    }
}
