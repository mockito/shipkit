package org.shipkit.internal.gradle.exec

import org.gradle.api.GradleException
import org.gradle.process.ExecResult
import spock.lang.Specification

class ExecCommandFactoryTest extends Specification {

    def result = Mock(ExecResult)

    def "ensures command succeeded"() {
        result.exitValue >> 0

        expect:
        ExecCommandFactory.ensureSucceeded(result, "[git] ")
    }

    def "throws exception if command fails"() {
        given:
        result.exitValue >> -100

        when:
        ExecCommandFactory.ensureSucceeded(result, "[git] ")

        then:
        def e = thrown(GradleException)
        e.message == """External command failed with exit code -100
Please inspect the command output prefixed with '[git]' the build log."""
    }
}
