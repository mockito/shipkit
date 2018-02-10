package org.shipkit.internal.gradle.util

import spock.lang.Specification

class OptionalTest extends Specification {

    def "does not accept nulls"() {
        when: Optional.of(null)
        then: thrown(IllegalArgumentException)
    }

    def "wraps optional value"() {
        def foo = Optional.of("foo")
        expect:
        foo.isPresent()
        foo.get() == "foo"
    }
}
