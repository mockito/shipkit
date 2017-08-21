package org.shipkit.internal.gradle.downstream.test

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class DownstreamTestingPluginTest extends Specification {

    def project = new ProjectBuilder().build()

    def "should apply plugin and create downstreamTest task"() {
        when:
        project.plugins.apply(DownstreamTestingPlugin)

        then:
        project.tasks.downstreamTest
    }

}
