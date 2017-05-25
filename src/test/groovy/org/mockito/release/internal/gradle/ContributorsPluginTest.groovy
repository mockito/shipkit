package org.mockito.release.internal.gradle

import testutil.PluginSpecification

class ContributorsPluginTest extends PluginSpecification {

    def "applies"() {
        given:
        def conf = applyReleaseConfiguration()
        conf.gitHub.readOnlyAuthToken = "token"
        conf.gitHub.repository = "repo"

        expect:
        project.plugins.apply("org.mockito.release-tools.contributors")
    }
}
