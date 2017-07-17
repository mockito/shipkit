package org.shipkit.internal.gradle.git

import org.gradle.testfixtures.ProjectBuilder
import org.shipkit.internal.exec.ProcessRunner
import spock.lang.Specification

class GitCheckOutTaskTest extends Specification {

    def "should run checkout"() {
        given:
        def task = new ProjectBuilder().build().tasks.create("checkout", GitCheckOutTask)
        def processRunner = Mock(ProcessRunner)
        task.processRunner = processRunner
        task.rev = "master"

        when:
        task.checkOut()

        then:
        1 * processRunner.run(["git", "checkout", "master"])
    }

    def "should run checkout with new branch"() {
        given:
        def task = new ProjectBuilder().build().tasks.create("checkout", GitCheckOutTask)
        def processRunner = Mock(ProcessRunner)
        task.processRunner = processRunner
        task.rev = "master"
        task.newBranch = true

        when:
        task.checkOut()

        then:
        1 * processRunner.run(["git", "checkout", "-b", "master"])
    }
}
