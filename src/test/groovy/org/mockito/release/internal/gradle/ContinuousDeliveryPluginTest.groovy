package org.mockito.release.internal.gradle

import testutil.PluginSpecification

class ContinuousDeliveryPluginTest extends PluginSpecification {

    def "applies"() {
        expect:
        project.plugins.apply("org.mockito.mockito-release-tools.continuous-delivery")
    }
}
