package org.shipkit.internal.gradle.git

import testutil.PluginSpecification

class GitSetupPluginTest extends PluginSpecification {

    def "applies"() {
        expect:
        project.plugins.apply(GitSetupPlugin)
    }
}
