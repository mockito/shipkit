package org.shipkit.internal.gradle.git

import testutil.PluginSpecification

class GitPluginTest extends PluginSpecification {

    def "applies"() {
        expect:
        project.plugins.apply(GitPlugin)
    }

}
