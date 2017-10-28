package org.shipkit.internal.gradle.downstream.test

import org.gradle.testfixtures.ProjectBuilder
import org.shipkit.internal.gradle.release.tasks.UploadGistsTask
import spock.lang.Specification

class TestTaskTestDownstream extends Specification {

    def project = new ProjectBuilder().build()

    def "should extract project name correctly"() {
        given:
        TestDownstreamTask task = project.tasks.create("testDownstream", TestDownstreamTask)
        task.setUploadGistsTask(project.tasks.create("uploadGists", UploadGistsTask))

        when:
        task.addRepository("https://github.com/mockito/mockito")

        then:
        project.tasks.testMockitoMockito
        project.tasks.cloneProjectFromGitHubMockitoMockito
        project.tasks.cloneProjectToWorkDirMockitoMockito
    }

    def "should extract project name correctly when slash is the last char in url"() {
        given:
        TestDownstreamTask task = project.tasks.create("testDownstream", TestDownstreamTask)
        task.setUploadGistsTask(project.tasks.create("uploadGists", UploadGistsTask))

        when:
        task.addRepository("https://github.com/mockito/shipkit-example/")

        then:
        project.tasks."testMockitoShipkitExample"
        project.tasks."cloneProjectFromGitHubMockitoShipkitExample"
        project.tasks."cloneProjectToWorkDirMockitoShipkitExample"
    }

}
