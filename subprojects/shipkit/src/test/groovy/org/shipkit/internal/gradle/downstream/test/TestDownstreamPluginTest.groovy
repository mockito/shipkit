package org.shipkit.internal.gradle.downstream.test

import org.shipkit.internal.gradle.release.CiContext
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
        (uploadGists.filesToUpload*.name as TreeSet).toString() == "[a.log, b.log]"
        project.tasks.testDownstream.uploadGistsTask == uploadGists
    }

    def "should enable uploadGists task when CI build and ghWriteToken set"() {
        given:
        def ciContext = Mock(CiContext)
        ciContext.ciBuild >> true
        conf.gitHub.writeAuthToken = "writeToken"

        when:
        def testDownstreamPlugin = new TestDownstreamPlugin(ciContext)
        testDownstreamPlugin.apply(project)

        then:
        project.tasks.uploadGists.enabled
    }

    def "should disable uploadGists task when ghWriteToken null"() {
        given:
        def ciContext = Mock(CiContext)
        conf.gitHub.writeAuthToken = null

        when:
        def testDownstreamPlugin = new TestDownstreamPlugin(ciContext)
        testDownstreamPlugin.apply(project)

        then:
        !project.tasks.uploadGists.enabled
    }

    def "should disable uploadGists task when not CI build"() {
        given:
        def ciContext = Mock(CiContext)
        ciContext.ciBuild >> false
        conf.gitHub.writeAuthToken = "writeToken"

        when:
        def testDownstreamPlugin = new TestDownstreamPlugin(ciContext)
        testDownstreamPlugin.apply(project)

        then:
        !project.tasks.uploadGists.enabled
    }
}
