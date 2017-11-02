package org.shipkit.internal.gradle.downstream.test

import org.shipkit.internal.gradle.release.tasks.UploadGistsTask
import testutil.PluginSpecification

class TestDownstreamTaskTest extends PluginSpecification {

    def "should construct correct log message when uploadGists enabled"() {
        when:
        def result = TestDownstreamTask.testDownstreamLogMessage("repo", null, true, "uploadGists")

        then:
        result == "Run test of repo. The output will be uploaded to Gist, search for logs of 'uploadGists' task to see the access link."
    }

    def "should construct correct log message when uploadGists disabled"() {
        when:
        def result = TestDownstreamTask.testDownstreamLogMessage("repo", tmp.root, false, null)

        then:
        result == "Run test of repo. The output will be saved in ${tmp.root.absolutePath}"
    }

    def "should extract project name correctly"() {
        given:
        TestDownstreamTask task = project.tasks.create("testDownstream", TestDownstreamTask)
        task.setUploadGistsTask(project.tasks.create("uploadGists", UploadGistsTask))

        when:
        task.addRepository("https://github.com/mockito/mockito")

        then:
        project.tasks.testMockitoMockito
        project.tasks.cloneMockitoMockito
    }

    def "should extract project name correctly when slash is the last char in url"() {
        given:
        TestDownstreamTask task = project.tasks.create("testDownstream", TestDownstreamTask)
        task.setUploadGistsTask(project.tasks.create("uploadGists", UploadGistsTask))

        when:
        task.addRepository("https://github.com/mockito/shipkit-example/")

        then:
        project.tasks."testMockitoShipkitExample"
        project.tasks."cloneMockitoShipkitExample"
    }
}
