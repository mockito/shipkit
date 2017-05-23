package org.mockito.release.internal.gradle

import testutil.PluginSpecification

class GitPluginTest extends PluginSpecification {

    def "applies"() {
        expect:
        project.plugins.apply(GitPlugin)
    }

}
