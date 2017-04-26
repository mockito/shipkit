package org.mockito.release.internal.gradle

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class BaseJavaLibraryPluginTest extends Specification {

    def root = new ProjectBuilder().build()
    def project = new ProjectBuilder().withParent(root).build()

    def "applies"() {
        root.plugins.apply("org.mockito.mockito-release-tools.continuous-delivery")

        expect:
        project.plugins.apply("org.mockito.mockito-release-tools.base-java-library")
    }
}
