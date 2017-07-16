package org.shipkit.internal.gradle.contributors

import testutil.PluginSpecification

class GithubContributorsPluginTest extends PluginSpecification {

    def "applies"() {
        expect:
        project.plugins.apply("org.shipkit.contributors")
    }
}
