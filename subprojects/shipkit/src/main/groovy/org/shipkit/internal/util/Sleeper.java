package org.shipkit.internal.util;

import java.util.concurrent.TimeUnit;

class Sleeper {

    void sleep(long seconds) throws InterruptedException {
        Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
    }
}
