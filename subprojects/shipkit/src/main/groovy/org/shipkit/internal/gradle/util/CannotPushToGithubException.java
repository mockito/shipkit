package org.shipkit.internal.gradle.util;

import org.gradle.api.GradleException;
import org.shipkit.gradle.git.GitPushTask;

public class CannotPushToGithubException extends RuntimeException {
    public static final String GH_WRITE_TOKEN_NOT_SET_MSG = "GH_WRITE_TOKEN not set and you don't have access to push to remote. which is a possible issue. Please recheck your configuration.";
    public static final String GH_WRITE_TOKEN_INVALID_MSG = "GH_WRITE_TOKEN is set but possibly invalid. Please recheck your configuration.";

    private CannotPushToGithubException(String message, Throwable cause) {
        super(message, cause);
    }

    public static CannotPushToGithubException create(Exception e, GitPushTask task) {
        String message;
        if (task.getSecretValue() == null) {
            message = GH_WRITE_TOKEN_NOT_SET_MSG;
        } else {
            message = GH_WRITE_TOKEN_INVALID_MSG;
        }
        return new CannotPushToGithubException(message, e);
    }

    public static boolean matchException(GradleException e) {
        return e.getMessage().contains("Authentication failed") || e.getMessage().contains("Permission");
    }
}
