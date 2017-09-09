package org.shipkit.internal.gradle.java

import testutil.PluginSpecification

class JavaBintrayPluginTest extends PluginSpecification {

    def "applies"() {
        expect:
        project.plugins.apply(JavaBintrayPlugin)
    }
}
