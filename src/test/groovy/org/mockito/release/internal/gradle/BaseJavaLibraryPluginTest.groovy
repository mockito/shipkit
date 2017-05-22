package org.mockito.release.internal.gradle

import testutil.PluginSpecification

class BaseJavaLibraryPluginTest extends PluginSpecification {

    def "applies"() {
        expect:
        project.plugins.apply("org.mockito.mockito-release-tools.base-java-library")
    }
}
