package org.shipkit.internal.util;

import java.util.function.Consumer;

class RetryManager {

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

    static RetryManager defaultRetryValues() {
        return new RetryManager(20, 10);
    }

    static RetryManager customRetryValues(int numberOfPossibleRetries, long timeoutLength) {
        return new RetryManager(numberOfPossibleRetries, timeoutLength);
    }

    boolean shouldRetry() {
        return timeoutsCount < numberOfRetries;
    }

    void waitNow(Consumer<Long> waitingConsumer) throws InterruptedException {
        computeWaitingValues();
        waitByConsumer(waitingConsumer);
    }

    private void computeWaitingValues() {
        alreadyWaitingTime += waitTime;
        waitTime += timeoutLength;
        timeoutsCount++;
    }

    private void waitByConsumer(Consumer<Long> waitingConsumer) throws InterruptedException {
        waitingConsumer.accept(waitTime);
    }

    String describe() {
        return String.format("Waiting time so far: %d seconds. Waiting %d seconds...", alreadyWaitingTime, waitTime);
    }

    boolean timeoutHappened() {
        return timeoutsCount > 0;
    }
}
