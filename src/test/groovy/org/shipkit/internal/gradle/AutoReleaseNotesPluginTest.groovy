package org.shipkit.internal.gradle

import testutil.PluginSpecification

class AutoReleaseNotesPluginTest extends PluginSpecification {
    def "applies"() {
        expect:
        project.plugins.apply("org.shipkit.auto-release-notes")
    }

}
