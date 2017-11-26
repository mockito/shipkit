package org.shipkit.internal.gradle.git

import testutil.PluginSpecification

class GitConfigPluginTest extends PluginSpecification {

    def "applies"() {
        expect:
        project.plugins.apply(GitConfigPlugin)
    }
}
