package org.shipkit.internal.gradle

import testutil.PluginSpecification

class ContinuousDeliveryPluginTest extends PluginSpecification {

    def "applies"() {
        expect:
        project.plugins.apply("org.shipkit.continuous-delivery")
    }
}
