package org.shipkit.internal.gradle.e2e

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class E2ETestTaskTest extends Specification {

    def project = new ProjectBuilder().build()

    def "should extract project name correctly"() {
        when:
        E2ETestTask task = project.tasks.create("e2eTest", E2ETestTask)
        task.addRepository("https://github.com/mockito/mockito")

        then:
        project.tasks.testMockito
        project.tasks.cloneProjectFromGitHubMockito
        project.tasks.cloneProjectToWorkDirMockito
    }

    def "should extract project name correctly when slash is the last char in url"() {
        when:
        E2ETestTask task = project.tasks.create("e2eTest", E2ETestTask)
        task.addRepository("https://github.com/mockito/shipkit-example/")

        then:
        project.tasks."testShipkit-example"
        project.tasks."cloneProjectFromGitHubShipkit-example"
        project.tasks."cloneProjectToWorkDirShipkit-example"
    }

}
