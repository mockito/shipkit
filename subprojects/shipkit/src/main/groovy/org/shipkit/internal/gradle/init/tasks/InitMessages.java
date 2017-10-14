package org.shipkit.internal.gradle.init.tasks;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

/**
 * Ensures consistent messages logged out from execution of Shipkit
 */
public class InitMessages {

    private static final Logger LOG = Logging.getLogger(InitMessages.class);

    public static void skipping(String filePath) {
        LOG.lifecycle("File exists, skipping generation of {}", filePath);
    }

    public static void generated(String fileAbsolutePath) {
        LOG.lifecycle("Generated (please review and check in!): {}", fileAbsolutePath);
    }
}
