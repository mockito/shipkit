package org.shipkit.internal.gradle

import testutil.PluginSpecification

class BaseJavaLibraryPluginTest extends PluginSpecification {

    def "applies"() {
        expect:
        project.plugins.apply("org.shipkit.base-java-library")
    }
}
