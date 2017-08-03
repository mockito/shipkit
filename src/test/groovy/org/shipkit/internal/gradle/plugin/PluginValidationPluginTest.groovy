package org.shipkit.internal.gradle.plugin

import testutil.PluginSpecification


class PluginValidationPluginTest extends PluginSpecification {

    def "apply"() {
        expect:
        project.plugins.apply(PluginValidationPlugin)
    }
}
