package org.shipkit.internal.exec

import spock.lang.Specification

class ExternalProcessStreamTest extends Specification {

    def output = new ByteArrayOutputStream()

    def "decorates output"() {
        def s = new ExternalProcessStream("[./gradlew] ", new PrintStream(output))
        when:
        s.write('hey\nbuddy'.bytes)

        then:
        output.toString() == """[./gradlew] hey
[./gradlew] buddy"""
    }
}
