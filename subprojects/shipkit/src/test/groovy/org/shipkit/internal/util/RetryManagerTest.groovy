package org.shipkit.internal.util

import spock.lang.Specification

class RetryManagerTest extends Specification {

    def "should retry when retries limit not reached"() {
        given:
        RetryManager retryCounter = RetryManager.defaultRetryValues(new Sleeper());

        expect:
        retryCounter.shouldRetry()
    }

    def "should not retry when retries limit reached"() {
        given:
        def sleeper = Mock(Sleeper) {
            sleep(_) >> null
        }
        RetryManager retryCounter = RetryManager.defaultRetryValues(sleeper)
        for (int i = 0; i < 20; i++) {
            retryCounter.waitNow()
        }

        expect:
        !retryCounter.shouldRetry()
    }

    def "providing correct description"() {
        given:
        def sleeper = Mock(Sleeper) {
            sleep(_) >> null
        }
        RetryManager retryCounter = RetryManager.defaultRetryValues(sleeper)
        for (int i = 0; i < 7; i++) {
            retryCounter.waitNow()
        }

        expect:
        retryCounter.describe() == "Waiting time so far: 210 seconds. Waiting 70 seconds..."
    }

    def "should provide correct information when timeout happened"() {
        given:
        def sleeper = Mock(Sleeper) {
            sleep(_) >> null
        }
        RetryManager retryCounter = RetryManager.defaultRetryValues(sleeper)
        retryCounter.waitNow()

        expect:
        retryCounter.timeoutHappened()
    }

    def "should provide correct information when no timeout happened"() {
        given:
        RetryManager retryCounter = RetryManager.defaultRetryValues(new Sleeper())

        expect:
        !retryCounter.timeoutHappened()
    }
}
