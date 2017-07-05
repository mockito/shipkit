package org.shipkit.internal.gradle.release

import testutil.PluginSpecification

class GradlePortalReleasePluginTest extends PluginSpecification {

    def "applies"() {
        expect:
        project.plugins.apply(GradlePortalReleasePlugin.class)
    }
}
