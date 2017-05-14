package org.mockito.release.internal.gradle

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class E2ETestingPluginTest extends Specification {

    def project = new ProjectBuilder().build()

    def "e2e"() {
        project.plugins.apply("org.mockito.mockito-release-tools.e2e-test")
        println project.tasks

//        when:
//        project.e2eTest.create("https://github.com/mockito/mockito-release-tools-example")

        expect:
        project.tasks.'runTestmockito-release-tools-example'
    }
}
