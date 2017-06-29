package org.shipkit.gradle.exec

import org.gradle.api.GradleException
import org.gradle.process.ExecResult
import spock.lang.Specification

class ExecCommandTest extends Specification {

    def result = Mock(ExecResult)

    def "ensures command succeeded"() {
        result.exitValue >> 0

        expect:
        ExecCommand.ensureSucceeded(result)
    }

    def "throws exception if command fails"() {
        given:
        result.exitValue >> -100

        when:
        ExecCommand.ensureSucceeded(result)

        then:
        def e = thrown(GradleException)
        e.message.contains("-100")
    }
}
