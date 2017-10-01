package org.shipkit.internal.gradle.plugin.tasks

import testutil.PluginSpecification

import static org.shipkit.internal.gradle.plugin.PluginDiscoveryPluginTest.META_INF_GRADLE_PLUGINS

class PluginUtilTest extends PluginSpecification {

    def "implementation class"() {
        when:
        project.file(META_INF_GRADLE_PLUGINS).mkdirs()
        File file = project.file("$META_INF_GRADLE_PLUGINS/org.shipkit.plugin-name-sample.properties") << "implementation-class=org.shipkit.PluginNameSample"
        then:
        PluginUtil.getImplementationClass(file) == 'org.shipkit.PluginNameSample'
    }
}
