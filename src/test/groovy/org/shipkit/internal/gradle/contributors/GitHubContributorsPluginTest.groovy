package org.shipkit.internal.gradle.contributors

import testutil.PluginSpecification

class GitHubContributorsPluginTest extends PluginSpecification {

    def "applies"() {
        expect:
        project.plugins.apply("org.shipkit.git-hub-contributors")
    }
}
