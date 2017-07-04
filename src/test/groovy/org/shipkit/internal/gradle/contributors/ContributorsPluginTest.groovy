package org.shipkit.internal.gradle.contributors

import testutil.PluginSpecification

class ContributorsPluginTest extends PluginSpecification {

    def "applies"() {
        expect:
        project.plugins.apply("org.shipkit.contributors")
    }
}
