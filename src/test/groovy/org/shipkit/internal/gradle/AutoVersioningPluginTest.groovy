package org.shipkit.internal.gradle

import testutil.PluginSpecification

class AutoVersioningPluginTest extends PluginSpecification {

    def "applies"() {
        expect:
        project.plugins.apply(AutoVersioningPlugin)
    }

}
