package org.shipkit.internal.notes.contributors

import org.shipkit.internal.notes.util.Function
import spock.lang.Specification

class ConcurrentDispatcherTest extends Specification {

    def "Dispatch"() {
        given:
        def list = (1 .. 100).toList()
        def listSize = list.size()

        def functionMock = Mock(Function)
        functionMock.apply(_) >>> list.collect { it + 5 }

        when:
        Set result = new ConcurrentDispatcher().dispatch(functionMock, list)

        then:
        result
        result.size() == listSize
        result == (6 .. 105).toSet()
    }

    def "dispatch using empty list"() {
        def list = []
        def functionMock = Mock(Function)

        when:
        Set result = new ConcurrentDispatcher().dispatch(functionMock, list)

        then:
        result.isEmpty()
    }
}
