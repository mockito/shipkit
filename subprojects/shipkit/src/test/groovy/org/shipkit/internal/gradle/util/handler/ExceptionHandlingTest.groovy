package org.shipkit.internal.gradle.util.handler

import org.gradle.api.Action
import spock.lang.Specification

import static org.shipkit.internal.gradle.util.handler.ExceptionHandling.withExceptionHandling

class ExceptionHandlingTest extends Specification {

    def runnable = Mock(Runnable)
    def handler = Mock(Action)

    def "no exception"() {
        when:
        withExceptionHandling(runnable, handler)

        then:
        1 * runnable.run()
        0 * _
    }

    def "delegates exception handling"() {
        def e = new RuntimeException("Boo!")

        when:
        withExceptionHandling(runnable, handler)

        then:
        1 * runnable.run() >> { throw e }
        1 * handler.execute(e)
        0 * _
    }
}
