package org.shipkit.internal.util;

class RetryManager {

    private final int numberOfRetries;
    private final long timeoutLength;
    private final Sleeper sleeper;
    private int timeoutsCount;
    private long alreadyWaitingTime;
    private long waitTime;

    private RetryManager(int numberOfPossibleRetries, long timeoutLength, Sleeper sleeper) {
        this.numberOfRetries = numberOfPossibleRetries;
        this.timeoutLength = timeoutLength;
        this.timeoutsCount = 0;
        this.alreadyWaitingTime = 0;
        this.waitTime = 0;
        this.sleeper = sleeper;
    }

    static RetryManager defaultRetryValues(Sleeper sleeper) {
        return new RetryManager(20, 10, sleeper);
    }

    static RetryManager customRetryValues(int numberOfPossibleRetries, long timeoutLength, Sleeper sleeper) {
        return new RetryManager(numberOfPossibleRetries, timeoutLength, sleeper);
    }

    boolean shouldRetry() {
        return timeoutsCount < numberOfRetries;
    }

    void waitNow() throws InterruptedException {
        computeWaitingValues();
        sleep();
    }

    private void computeWaitingValues() {
        alreadyWaitingTime += waitTime;
        timeoutsCount++;
        waitTime = timeoutLength * timeoutsCount;
    }

    private void sleep() throws InterruptedException {
        sleeper.sleep(waitTime);
    }

    String describe() {
        return String.format("Waiting time so far: %d seconds. Waiting %d seconds...", alreadyWaitingTime, waitTime);
    }

    boolean timeoutHappened() {
        return timeoutsCount > 0;
    }
}
