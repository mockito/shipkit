package org.shipkit.internal.notes.contributors

import org.shipkit.internal.notes.util.Function
import spock.lang.Specification

class FetcherCallableTest extends Specification {

    def "call"() {
        given:
        def list = (1 .. 5).toList()
        def functionMock = Mock(Function)

        functionMock.apply(_) >>> list.collect { it + 5 }

        when:
        def callable = new FetcherCallable(list, functionMock)
        Set result = callable.call()

        then:
        result
        result == [6, 7, 8, 9, 10] as Set
    }

    def "call using empty list"() {
        given:
        def list = []
        def functionMock = Mock(Function)

        when:
        def callable = new FetcherCallable(list, functionMock)
        Set result = callable.call()

        then:
        result.isEmpty()
    }
}
