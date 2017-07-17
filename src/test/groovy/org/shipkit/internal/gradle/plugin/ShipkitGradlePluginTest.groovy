package org.shipkit.internal.gradle.plugin

import testutil.PluginSpecification

class ShipkitGradlePluginTest extends PluginSpecification {

    def "applies"(){
        expect:
        project.plugins.apply("org.shipkit.gradle-plugin")
    }
}
