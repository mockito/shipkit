package org.shipkit.internal.gradle.util.handler

import org.gradle.api.GradleException
import org.shipkit.internal.gradle.util.handler.exceptions.CannotPushToGithubException
import spock.lang.Specification

class ProcessExceptionHandlerTest extends Specification {

    def "should process without errors for lack of exceptions"() {
        given:
            def processExceptionHandler = new ProcessExceptionHandler()
            def processRunnable = Mock(Runnable)
        when:
            processExceptionHandler.runProcessExceptionally(processRunnable)
        then:
            noExceptionThrown()
            1 * processRunnable.run()
            0 * _
    }

    def "should rethrow Gradle Exception if no handler matched"() {
        given:
            def processExceptionHandler = new ProcessExceptionHandler()
            def originalException = new GradleException("original")

            def taskExceptionHandler = Mock(TaskExceptionHandler)
            processExceptionHandler.addHandler(taskExceptionHandler)

            1 * taskExceptionHandler.matchException(originalException) >> false

            def processRunnable = Mock(Runnable)
            1 * processRunnable.run() >> {throw originalException}
            0 * _
        when:
            processExceptionHandler.runProcessExceptionally(processRunnable)
        then:
            def e = thrown(GradleException)
            e == originalException
    }

    def "should not handle if no Gradle Exception"() {
        given:
            def taskExceptionHandler = Mock(TaskExceptionHandler)
            def originalException = new IllegalArgumentException("original")

            def processExceptionHandler = new ProcessExceptionHandler()
            processExceptionHandler.addHandler(taskExceptionHandler)

            def processRunnable = Mock(Runnable)
            1 * processRunnable.run() >> {throw originalException}
            0 * _
        when:
            processExceptionHandler.runProcessExceptionally(processRunnable)
        then:
            def e = thrown(IllegalArgumentException)
            e == originalException
    }

    def "should handle properly and throw new exception"() {
        given:
            def originalException = new GradleException("original")
            def newException = new CannotPushToGithubException("new", new Throwable())

            def taskExceptionHandler = Mock(TaskExceptionHandler)
            def processExceptionHandler = new ProcessExceptionHandler()
            processExceptionHandler.addHandler(taskExceptionHandler)

            1 * taskExceptionHandler.matchException(originalException) >> true
            1 * taskExceptionHandler.create(originalException) >> newException

            def processRunnable = Mock(Runnable)
            1 * processRunnable.run() >> {throw originalException}
            0 * _
        when:
            processExceptionHandler.runProcessExceptionally(processRunnable)
        then:
            def e = thrown(CannotPushToGithubException)
            e == newException
    }

}
