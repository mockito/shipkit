package org.shipkit.internal.gradle.java

import testutil.PluginSpecification

class ShipkitJavaPluginTest extends PluginSpecification {

    def "applies"() {
        expect:
        project.plugins.apply("org.shipkit.java")
    }
}
