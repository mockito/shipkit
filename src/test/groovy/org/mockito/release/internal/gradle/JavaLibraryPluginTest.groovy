package org.mockito.release.internal.gradle

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class JavaLibraryPluginTest extends Specification {

    def root = new ProjectBuilder().build()
    def project = new ProjectBuilder().withParent(root).build()

    def "applies"() {
        root.plugins.apply("org.mockito.mockito-release-tools.continuous-delivery")
        project.ext.bintray_repo = "my-repo"
        project.ext.bintray_pkg = "my-pkg"

        expect:
        project.plugins.apply("org.mockito.mockito-release-tools.java-library")
    }
}
