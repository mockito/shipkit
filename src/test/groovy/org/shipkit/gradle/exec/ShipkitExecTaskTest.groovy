package org.shipkit.gradle.exec

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

import static org.shipkit.internal.gradle.exec.ExecCommandFactory.execCommand

class ShipkitExecTaskTest extends Specification {

    def project = new ProjectBuilder().build()

    def "executes with clean output"() {
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
}
