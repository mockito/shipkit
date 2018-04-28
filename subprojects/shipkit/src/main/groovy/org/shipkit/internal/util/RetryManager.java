package org.shipkit.internal.util;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.util.concurrent.TimeUnit;

class RetryManager {

    private static final Logger LOG = Logging.getLogger(RetryManager.class);

    private final int numberOfRetries;
    private final long timeoutLength;

    private int timeoutsCount;
    private long alreadyWaitingTime;
    private long waitTime;

    private RetryManager(int numberOfPossibleRetries, long timeoutLength) {
        this.numberOfRetries = numberOfPossibleRetries;
        this.timeoutLength = timeoutLength;
        this.timeoutsCount = 0;
        this.alreadyWaitingTime = 0;
        this.waitTime = 0;
    }

    static RetryManager newInstance() {
        return new RetryManager(20, 10);
    }

    static RetryManager of(int numberOfPossibleRetries, long timeoutLength) {
        return new RetryManager(numberOfPossibleRetries, timeoutLength);
    }

    boolean shouldRetry() {
        return timeoutsCount < numberOfRetries;
    }

    void waitNow() throws InterruptedException {
        computeWaitingValues();
        waitForRetry();
    }

    private void computeWaitingValues() {
        alreadyWaitingTime += waitTime;
        waitTime += timeoutLength;
        timeoutsCount++;
    }

    private void waitForRetry() {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(waitTime));
        } catch (InterruptedException e) {
            LOG.lifecycle("Waiting interrupted");
        }
    }

    String describe() {
        return String.format("Waiting time so far: %d seconds. Waiting %d seconds...", alreadyWaitingTime, waitTime);
    }

    boolean timeoutHappened() {
        return timeoutsCount > 0;
    }
}
