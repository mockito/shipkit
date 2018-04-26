package org.shipkit.internal.util;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.shipkit.internal.gradle.util.StringUtil;

public class DeprecatedWarning {

    private static final Logger LOG = Logging.getLogger(DeprecatedWarning.class);
    private static final String DEPRECATED_PREFIX = "  [DEPRECATED]";

    public static void warn(String taskName, String explanation) {
        LOG.lifecycle("{} '{}' task is deprecated and may be removed in a new major release.", DEPRECATED_PREFIX, taskName);
        if (!StringUtil.isEmpty(explanation)) {
            LOG.lifecycle("{} {}", DEPRECATED_PREFIX, explanation);
        }
    }
}
