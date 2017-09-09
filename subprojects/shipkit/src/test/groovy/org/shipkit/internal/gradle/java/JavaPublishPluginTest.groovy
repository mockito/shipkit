package org.shipkit.internal.gradle.java

import testutil.PluginSpecification

class JavaPublishPluginTest extends PluginSpecification {

    def "applies"() {
        expect:
        project.plugins.apply(JavaPublishPlugin.class)
    }
}
