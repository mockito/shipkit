package org.shipkit.internal.gradle.java

import testutil.PluginSpecification

class GitHubPomContributorsPluginTest extends PluginSpecification {

    def "applies"() {
        expect:
        project.plugins.apply(GitHubPomContributorsPlugin)
    }
}
