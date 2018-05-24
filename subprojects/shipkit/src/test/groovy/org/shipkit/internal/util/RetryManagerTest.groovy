package org.shipkit.internal.util

import spock.lang.Specification

class RetryManagerTest extends Specification {

    def "should retry when retries limit not reached"() {
        given:
        RetryManager retryCounter = RetryManager.of(20, 1)

        expect:
        retryCounter.shouldRetry()
    }

    def "should not retry when retries limit reached"() {
        given:
        RetryManager retryCounter = RetryManager.of(2, 1)
        for (int i = 0; i < 2; i++) {
            retryCounter.waitNow()
        }

        expect:
        !retryCounter.shouldRetry()
    }

    def "providing correct description"() {
        given:
        RetryManager retryCounter = RetryManager.of(20, 1)
        for (int i = 0; i < 7; i++) {
            retryCounter.waitNow()
        }

        expect:
        retryCounter.describe() == "Waiting time so far: 21 seconds. Waiting 7 seconds..."
    }

    def "should provide correct information when timeout happened"() {
        given:
        RetryManager retryCounter = RetryManager.of(20, 1)
        retryCounter.waitNow()

        expect:
        retryCounter.timeoutHappened()
    }

    def "should provide correct information when no timeout happened"() {
        given:
        RetryManager retryCounter = RetryManager.of(20, 1)

        expect:
        !retryCounter.timeoutHappened()
    }
}
