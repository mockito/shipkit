package org.shipkit.gradle.exec

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class ShipkitExecTaskTest extends Specification {

    def project = new ProjectBuilder().build()

    def "executes with clean output"() {
        def t = (ShipkitExecTask) project.tasks.create("t", ShipkitExecTask)
        t.execCommands.add(new ExecCommand("Saying first", ["echo", "first"]))
        t.execCommands.add(new ExecCommand("Saying second", ["echo", "second"]))

        when:
        t.execute()

        then:
        //this is a smoke test. it's hard to write tests for this logic.
        //since the logic does not have complexity, let's have a smoke test for now and see how it goes
        noExceptionThrown()
    }
}
