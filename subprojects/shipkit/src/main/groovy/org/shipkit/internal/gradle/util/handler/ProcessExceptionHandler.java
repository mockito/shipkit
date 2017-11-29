package org.shipkit.internal.gradle.util.handler;

import java.util.ArrayList;
import java.util.List;

import org.gradle.api.GradleException;

public class ProcessExceptionHandler {
    private List<TaskExceptionHandler> exceptionHandlers = new ArrayList<TaskExceptionHandler>();

    public void runProcessExceptionally(Runnable processRunner) {
        try {
            processRunner.run();
        } catch (GradleException ex) {
            for (TaskExceptionHandler exceptionHandler : exceptionHandlers) {
                if(exceptionHandler.matchException(ex)) {
                    throw exceptionHandler.create(ex);
                }
            }
            throw ex;
        }
    }

    public void addHandler(TaskExceptionHandler taskExceptionHandler) {
        exceptionHandlers.add(taskExceptionHandler);
    }
}
