package org.shipkit.gradle.git

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class IdentifyGitBranchTaskTest extends Specification {

    def project = new ProjectBuilder().build()

    def "identifies branch"() {
        def t = project.tasks.create("identify", IdentifyGitBranchTask)
        t.workDir = new File(System.getProperty("user.dir"))

        when:
        t.execute()

        then:
        !t.branch.isEmpty()
    }

    def "fails when branch requested too early"() {
        def t = project.tasks.create("identify", IdentifyGitBranchTask)

        when:
        t.branch

        then:
        thrown(IdentifyGitBranchTask.BranchNotAvailableException)
    }
}
