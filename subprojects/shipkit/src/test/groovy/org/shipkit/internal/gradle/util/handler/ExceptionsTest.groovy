package org.shipkit.internal.gradle.util.handler

import org.gradle.api.Action
import org.gradle.api.GradleException
import org.shipkit.internal.gradle.util.handler.exceptions.CannotPushToGithubException
import spock.lang.Specification

class ExceptionsTest extends Specification {

    def "should process without errors for lack of exceptions"() {
        given:
            def processRunnable = Mock(Runnable)
            def action = Mock(Action)
        when:
            Exceptions.handling(processRunnable, action)
        then:
            noExceptionThrown()
            1 * processRunnable.run()
            0 * _
    }

    def "should rethrow Gradle Exception if no handler matched"() {
        given:
            def processExceptionHandler = new Exceptions()
            def originalException = new GradleException("original")
            def action = Mock(Action)

            def processRunnable = Mock(Runnable)
            1 * processRunnable.run() >> {throw originalException}
            1 * action.execute(originalException)
            0 * _
        when:
            processExceptionHandler.handling(processRunnable, action)
        then:
            def e = thrown(GradleException)
            e == originalException
    }

    def "should not handle if no Gradle Exception"() {
        given:
            def originalException = new IllegalArgumentException("original")
            def action = Mock(Action)

            def exceptions = new Exceptions()
            def processRunnable = Mock(Runnable)
            1 * processRunnable.run() >> {throw originalException}
            0 * _
        when:
            exceptions.handling(processRunnable, action)
        then:
            def e = thrown(IllegalArgumentException)
            e == originalException
    }

    def "should handle properly and throw new exception"() {
        given:
            def originalException = new GradleException("original")
            def newException = new CannotPushToGithubException("new", new Throwable())

            def action = Mock(Action)
            def processExceptionHandler = new Exceptions()

            1 * action.execute(originalException) >> {throw newException}

            def processRunnable = Mock(Runnable)
            1 * processRunnable.run() >> {throw originalException}
            0 * _
        when:
            processExceptionHandler.handling(processRunnable, action)
        then:
            def e = thrown(CannotPushToGithubException)
            e == newException
    }

}
