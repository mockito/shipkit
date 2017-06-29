package org.shipkit.internal.gradle.exec

import org.gradle.api.GradleException
import org.gradle.process.ExecResult
import spock.lang.Specification

class ExecCommandFactoryTest extends Specification {

    def result = Mock(ExecResult)

    def "ensures command succeeded"() {
        result.exitValue >> 0

        expect:
        ExecCommandFactory.ensureSucceeded(result)
    }

    def "throws exception if command fails"() {
        given:
        result.exitValue >> -100

        when:
        ExecCommandFactory.ensureSucceeded(result)

        then:
        def e = thrown(GradleException)
        e.message.contains("-100")
    }
}
