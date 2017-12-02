package org.shipkit.internal.gradle.util.handler;

import org.gradle.api.Action;

public class ExceptionHandling {

    public static void withExceptionHandling(Runnable code, Action<RuntimeException> handler) {
        try {
            code.run();
        } catch (RuntimeException e) {
            handler.execute(e);
        }
    }
}
