package org.shipkit.internal.gradle.util.handler;

import org.gradle.api.Action;
import org.gradle.api.GradleException;

public class Exceptions {

    public static void handling(Runnable code, Action<GradleException> handler) {
        try {
            code.run();
        } catch (GradleException e) {
            handler.execute(e);
            throw e;
        }
    }
}
