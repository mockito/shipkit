package org.mockito.release.internal.gradle

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class JavaLibraryPluginTest extends Specification {

    def project = new ProjectBuilder().build()

    def "applies"() {
        project.ext.bintray_repo = "my-repo"
        project.ext.bintray_pkg = "my-pkg"

        expect:
        project.plugins.apply("org.mockito.mockito-release-tools.java-library")
    }
}
