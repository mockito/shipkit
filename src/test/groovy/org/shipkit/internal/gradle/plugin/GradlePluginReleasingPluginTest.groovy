package org.shipkit.internal.gradle.plugin

import testutil.PluginSpecification

class GradlePluginReleasingPluginTest extends PluginSpecification {

    def "applies"(){
        expect:
        project.plugins.apply("org.shipkit.gradle-plugin")
    }
}
