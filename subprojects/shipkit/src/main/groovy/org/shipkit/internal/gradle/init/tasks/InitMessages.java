package org.shipkit.internal.gradle.init.tasks;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

public class InitMessages {

    private static final Logger LOG = Logging.getLogger(InitMessages.class);

    public static void skipping(String filePath, String taskPath) {
        LOG.lifecycle("{} - file exists, skipping generation of {}", taskPath, filePath);
    }
}
