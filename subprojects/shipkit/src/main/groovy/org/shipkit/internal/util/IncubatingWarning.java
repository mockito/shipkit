package org.shipkit.internal.util;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.util.function.Predicate;

public class IncubatingWarning {

    private final static Logger LOG = Logging.getLogger(IncubatingWarning.class);

    public static void warn(String feature, Predicate<String> incubatingWarningAcknowledged) {
        if (incubatingWarningAcknowledged.test(feature)) {
            LOG.lifecycle("  [INCUBATING] {} is incubating and may change in any version.", feature);
        }
    }
}
