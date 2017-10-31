package org.shipkit.internal.gradle.java

import testutil.PluginSpecification

class JavaLibraryPluginTest extends PluginSpecification {

    def "applies"() {
        expect:
        project.plugins.apply(JavaLibraryPlugin)
    }
}
