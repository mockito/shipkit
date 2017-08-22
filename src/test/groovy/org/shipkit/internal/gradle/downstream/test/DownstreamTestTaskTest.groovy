package org.shipkit.internal.gradle.downstream.test

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class DownstreamTestTaskTest extends Specification {

    def project = new ProjectBuilder().build()

    def "should extract project name correctly"() {
        when:
        DownstreamTestTask task = project.tasks.create("downstreamTest", DownstreamTestTask)
        task.addRepository("https://github.com/mockito/mockito")

        then:
        project.tasks.testMockito
        project.tasks.cloneProjectFromGitHubMockito
        project.tasks.cloneProjectToWorkDirMockito
    }

    def "should extract project name correctly when slash is the last char in url"() {
        when:
        DownstreamTestTask task = project.tasks.create("downstreamTest", DownstreamTestTask)
        task.addRepository("https://github.com/mockito/shipkit-example/")

        then:
        project.tasks."testShipkit-example"
        project.tasks."cloneProjectFromGitHubShipkit-example"
        project.tasks."cloneProjectToWorkDirShipkit-example"
    }

}
