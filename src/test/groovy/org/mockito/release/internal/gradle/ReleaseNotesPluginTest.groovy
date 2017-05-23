package org.mockito.release.internal.gradle

import testutil.PluginSpecification

class ReleaseNotesPluginTest extends PluginSpecification {

    def "applies cleanly"() {
        expect:
        project.plugins.apply("org.mockito.release-notes")
    }
}
