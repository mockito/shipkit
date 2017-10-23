package org.shipkit.internal.gradle.downstream.test

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
}
