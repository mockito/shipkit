package org.mockito.release.exec

import org.gradle.api.GradleException
import org.gradle.api.logging.Logger
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.IgnoreIf
import spock.lang.Specification

import static org.mockito.release.exec.TestUtil.commandAvailable

//ignore the test when there is no 'ls' utility
@IgnoreIf({!commandAvailable("ls")})
class DefaultProcessRunnerTest extends Specification {

    @Rule TemporaryFolder tmp = new TemporaryFolder()

    def "runs processes and returns output"() {
        File dir = tmp.newFolder()
        new File(dir, "xyz.txt").createNewFile()
        new File(dir, "hey joe.jar").createNewFile()

        when:
        String output = new DefaultProcessRunner(dir).run("ls")

        then:
        output.contains("xyz.txt")
        output.contains("hey joe.jar")
    }

    def "masks output"() {
        File dir = tmp.newFolder()

        when:
        def out = new DefaultProcessRunner(dir).setSecretValue("foobar").run("echo", "a foobar b foobar c")

        then:
        out.contains("a [SECRET] b [SECRET] c")
    }

    def "masks failure message"() {
        File dir = tmp.newFolder()

        when:
        new DefaultProcessRunner(dir).setSecretValue("foobar").run("ls", "foobar")

        then:
        def ex = thrown(GradleException)
        ex.message.contains("Execution of command failed")
        !ex.message.contains("foobar")
        ex.message.contains("[SECRET]")
    }

    def "masks logging"() {
        File dir = tmp.newFolder()
        def log = Mock(Logger)

        when:
        new DefaultProcessRunner(dir).setSecretValue("foobar").run(log, ["ls", "foobar", "xx foobar yy"])

        then:
        thrown(GradleException)
        log.lifecycle("ls [SECRET] xx [SECRET] yy")
    }
}
