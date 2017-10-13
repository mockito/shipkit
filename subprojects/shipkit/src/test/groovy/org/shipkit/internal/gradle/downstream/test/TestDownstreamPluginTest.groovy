package org.shipkit.internal.gradle.downstream.test

import org.shipkit.internal.gradle.release.tasks.UploadGistsTask
import testutil.PluginSpecification

class TestDownstreamPluginTest extends PluginSpecification {

    def "should apply plugin and create testDownstream task"() {
        when:
        project.plugins.apply(TestDownstreamPlugin)

        then:
        project.tasks.testDownstream
    }

    def "should find log files for uploadGists in log directory"() {
        given:
        project.setBuildDir(tmp.root)
        tmp.newFile("a.log")
        tmp.newFile("b.log")
        tmp.newFile("c.txt")

        when:
        project.plugins.apply(TestDownstreamPlugin)

        then:
        UploadGistsTask uploadGists = project.tasks.uploadGists
        uploadGists.filesToUpload.collect{ it.name } == ["a.log", "b.log"]
    }

}
