package org.shipkit.gradle.exec

import org.gradle.api.tasks.TaskExecutionException
import org.gradle.testfixtures.ProjectBuilder
import org.shipkit.internal.gradle.exec.ExecCommandFactory
import spock.lang.Specification

import static org.shipkit.internal.gradle.exec.ExecCommandFactory.execCommand

class ShipkitExecTaskTest extends Specification {

    def project = new ProjectBuilder().build()

    def "successful command"() {
        def t = (ShipkitExecTask) project.tasks.create("t", ShipkitExecTask)
        t.execCommands.add(execCommand("Saying first", ["echo", "first"]))
        t.execCommands.add(execCommand("Saying second", ["echo", "second"]))

        when:
        t.execute()

        then:
        //this is a smoke test. it's hard to write tests for this logic.
        //since the logic does not have complexity, let's have a smoke test for now and see how it goes
        noExceptionThrown()
    }

    def "failing command"() {
        def t = (ShipkitExecTask) project.tasks.create("t", ShipkitExecTask)
        t.execCommands.add(execCommand("good", ["ls"]))
        t.execCommands.add(execCommand("bad", ["ls", "missing file"]))

        when:
        t.execute()

        then:
        def ex = thrown(TaskExecutionException.class)
        ex.cause.message.startsWith "External process failed with exit code"
        ex.cause.message.endsWith "\nPlease inspect the command output prefixed with '[missing file]' the build log."
    }

    def "when first command is configured to stop execution, second command will not fail the entire task"() {
        def t = (ShipkitExecTask) project.tasks.create("t", ShipkitExecTask)
        t.execCommands.add(execCommand("bad", ["ls", "missing file"], ExecCommandFactory.stopExecution()))
        t.execCommands.add(execCommand("another bad", ["ls", "missing file"]))

        when:
        t.execute()

        then:
        noExceptionThrown()
    }
}
