package org.mockito.release.internal.gradle

import testutil.PluginSpecification

class JavaLibraryPluginTest extends PluginSpecification {

    def "applies"() {
        project.ext.bintray_repo = "my-repo"
        project.ext.bintray_pkg = "my-pkg"

        expect:
        project.plugins.apply("org.mockito.mockito-release-tools.java-library")
    }
}
