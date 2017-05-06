package org.mockito.release.internal.gradle

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class BaseJavaLibraryPluginTest extends Specification {

    def project = new ProjectBuilder().withParent().build()
    @Rule
    def TemporaryFolder tmp = new TemporaryFolder()

    def "applies"() {
        expect:
        project.plugins.apply("org.mockito.mockito-release-tools.base-java-library")
    }
}
