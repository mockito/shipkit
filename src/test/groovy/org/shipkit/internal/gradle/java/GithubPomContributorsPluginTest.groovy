package org.shipkit.internal.gradle.java

import testutil.PluginSpecification

class GithubPomContributorsPluginTest extends PluginSpecification {

    def "applies"() {
        expect:
        project.plugins.apply(GithubPomContributorsPlugin)
    }
}
