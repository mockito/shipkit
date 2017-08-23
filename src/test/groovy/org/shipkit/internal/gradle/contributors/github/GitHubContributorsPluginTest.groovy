package org.shipkit.internal.gradle.contributors.github

import testutil.PluginSpecification

class GitHubContributorsPluginTest extends PluginSpecification {

    def "applies"() {
        expect:
        project.plugins.apply(GitHubContributorsPlugin)
    }
}
