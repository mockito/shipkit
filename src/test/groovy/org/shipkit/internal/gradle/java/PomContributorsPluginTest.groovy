package org.shipkit.internal.gradle.java

import testutil.PluginSpecification

class PomContributorsPluginTest extends PluginSpecification {

    def "applies"() {
        expect:
        project.plugins.apply(PomContributorsPlugin)
    }
}
