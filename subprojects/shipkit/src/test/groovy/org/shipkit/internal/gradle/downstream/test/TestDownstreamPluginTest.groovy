package org.shipkit.internal.gradle.downstream.test

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class TestDownstreamPluginTest extends Specification {

    def project = new ProjectBuilder().build()

    def "should apply plugin and create testDownstream task"() {
        when:
        project.plugins.apply(TestDownstreamPlugin)

        then:
        project.tasks.testDownstream
    }

}
