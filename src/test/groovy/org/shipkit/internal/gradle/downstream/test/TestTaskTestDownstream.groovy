package org.shipkit.internal.gradle.downstream.test

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class TestTaskTestDownstream extends Specification {

    def project = new ProjectBuilder().build()

    def "should extract project name correctly"() {
        when:
        TestDownstreamTask task = project.tasks.create("testDownstream", TestDownstreamTask)
        task.addRepository("https://github.com/mockito/mockito")

        then:
        project.tasks.testMockitoMockito
        project.tasks.cloneProjectFromGitHubMockitoMockito
        project.tasks.cloneProjectToWorkDirMockitoMockito
    }

    def "should extract project name correctly when slash is the last char in url"() {
        when:
        TestDownstreamTask task = project.tasks.create("testDownstream", TestDownstreamTask)
        task.addRepository("https://github.com/mockito/shipkit-example/")

        then:
        project.tasks."testMockitoShipkitExample"
        project.tasks."cloneProjectFromGitHubMockitoShipkitExample"
        project.tasks."cloneProjectToWorkDirMockitoShipkitExample"
    }

}
