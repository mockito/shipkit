package org.shipkit.internal.gradle.util.handler

import org.gradle.api.Action
import spock.lang.Specification

class ExceptionsTest extends Specification {

    def runnable = Mock(Runnable)
    def handler = Mock(Action)

    def "no exception"() {
        when:
        Exceptions.handling(runnable, handler)

        then:
        1 * runnable.run()
        0 * _
    }

    def "delegates exception handling"() {
        def e = new RuntimeException("Boo!")

        when:
        Exceptions.handling(runnable, handler)

        then:
        1 * runnable.run() >> { throw e }
        1 * handler.execute(e)
        0 * _
    }
}
