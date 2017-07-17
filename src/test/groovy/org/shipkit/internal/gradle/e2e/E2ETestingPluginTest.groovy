package org.shipkit.internal.gradle.e2e

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class E2ETestingPluginTest extends Specification {

    def project = new ProjectBuilder().build()

    def "should apply plugin and create e2eTest task"() {
        when:
        project.plugins.apply(E2ETestingPlugin)

        then:
        project.tasks.e2eTest
    }

}
