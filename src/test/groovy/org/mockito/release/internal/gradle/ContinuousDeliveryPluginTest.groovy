package org.mockito.release.internal.gradle

import testutil.PluginSpecification

class ContinuousDeliveryPluginTest extends PluginSpecification {

    def "applies"() {
        given:
        def conf = project.plugins.apply(ReleaseConfigurationPlugin).configuration
        conf.gitHub.readOnlyAuthToken = "token"
        conf.gitHub.repository = "repo"
        expect:
        project.plugins.apply("org.mockito.mockito-release-tools.continuous-delivery")
    }
}
