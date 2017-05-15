package org.mockito.release.internal.gradle

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class E2ETestingPluginTest extends Specification {

    def project = new ProjectBuilder().build()

    def "e2e"() {
        project.plugins.apply(E2ETestingPlugin)

        when:
        project.e2eTest.create("mockito-release-tools-example")

        then:
        project.tasks.runTest
    }
}
