package org.shipkit.internal.gradle.git.tasks;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

public class TokenAvailabilityMessage {

    private final static Logger LOG = Logging.getLogger(TokenAvailabilityMessage.class);

    public static void logMessage(String context, String authToken) {
        String message = createMessage(context, authToken);
        LOG.lifecycle(message);
    }

    static String createMessage(String context, String authToken) {
        String message;
        if (authToken == null) {
            message = "  '" + context + "' uses GitHub write token";
        } else {
            message = "  '" + context + "' does not use GitHub write token because it was not specified";
        }
        return message;
    }
}
