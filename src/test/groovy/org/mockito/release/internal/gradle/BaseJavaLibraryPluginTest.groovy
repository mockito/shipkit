package org.mockito.release.internal.gradle

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class BaseJavaLibraryPluginTest extends Specification {

    def project = new ProjectBuilder().withParent().build()

    def "applies"() {
        expect:
        project.plugins.apply("org.mockito.mockito-release-tools.base-java-library")
    }
}
