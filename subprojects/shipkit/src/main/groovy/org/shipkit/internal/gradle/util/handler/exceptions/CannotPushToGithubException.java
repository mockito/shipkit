package org.shipkit.internal.gradle.util.handler.exceptions;

public class CannotPushToGithubException extends RuntimeException {
    public CannotPushToGithubException(String message, Throwable cause) {
        super(message, cause);
    }
}
