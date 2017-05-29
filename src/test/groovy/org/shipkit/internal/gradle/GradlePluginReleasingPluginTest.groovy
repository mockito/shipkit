package org.shipkit.internal.gradle

import testutil.PluginSpecification

class GradlePluginReleasingPluginTest extends PluginSpecification {

    def "applies"(){
        expect:
        project.plugins.apply("org.mockito.mockito-release-tools.gradle-plugin-releasing")
    }
}
