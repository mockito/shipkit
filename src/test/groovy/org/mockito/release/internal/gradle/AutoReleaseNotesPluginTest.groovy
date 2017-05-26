package org.mockito.release.internal.gradle

import testutil.PluginSpecification

class AutoReleaseNotesPluginTest extends PluginSpecification {
    def "applies"() {
        expect:
        project.plugins.apply("org.mockito.mockito-release-tools.auto-release-notes")
    }

}
