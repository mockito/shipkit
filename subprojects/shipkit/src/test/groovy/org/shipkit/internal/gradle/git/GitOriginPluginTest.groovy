package org.shipkit.internal.gradle.git

import testutil.PluginSpecification

class GitOriginPluginTest extends PluginSpecification {

    def "applies cleanly"() {
        expect:
        project.plugins.apply(GitOriginPlugin)
    }
}
