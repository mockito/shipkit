package org.shipkit.internal.gradle.exec

import org.gradle.api.GradleException
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
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
        e.message == """External process failed with exit code -100
Please inspect the command output prefixed with '[git]' the build log."""
    }

    def "exec command with custom working dir"() {
        def execSpec = Mock(ExecSpec)
        def dir = new File("foo")
        def command = ExecCommandFactory.execCommand("Doing stuff", dir, "git", "status")

        when:
        command.setupAction.execute(execSpec)

        then:
        1 * execSpec.setWorkingDir(dir)
        1 * execSpec.setIgnoreExitValue(true)
    }

    def "commands ignore result by default"() {
        def execSpec = Mock(ExecSpec)
        def command = ExecCommandFactory.execCommand("Doing stuff", "git", "status")

        when:
        command.setupAction.execute(execSpec)

        then:
        1 * execSpec.setIgnoreExitValue(true)
    }

    def "default prefix"() {
        expect:
        ExecCommandFactory.defaultPrefix(["git"]) == "[git] "
        ExecCommandFactory.defaultPrefix(["git", "status"]) == "[status] "
    }
}
