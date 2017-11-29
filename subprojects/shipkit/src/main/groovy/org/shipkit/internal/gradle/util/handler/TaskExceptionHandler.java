package org.shipkit.internal.gradle.util.handler;

import org.gradle.api.GradleException;

public interface TaskExceptionHandler {
    boolean matchException(GradleException ex);
    RuntimeException create(GradleException ex);
}
