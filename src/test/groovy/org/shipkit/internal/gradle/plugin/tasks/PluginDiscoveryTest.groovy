package org.shipkit.internal.gradle.plugin.tasks

import testutil.PluginSpecification
import static org.shipkit.internal.gradle.plugin.PluginDiscoveryPluginTest.META_INF_GRADLE_PLUGINS

class PluginDiscoveryTest extends PluginSpecification {

    def "generate plugin name"() {
        expect:
        PluginDiscovery.generatePluginName(input) == expected

        where:
        input                                       | expected
        'plugin.properties'                         | 'plugin'
        'com.shipkit.base-java-library.properties'  | 'baseJavaLibrary'
        'com.shipkit.versioning.properties'         | 'versioning'
    }

    def "implementation class"() {
        when:
        project.file(META_INF_GRADLE_PLUGINS).mkdirs()
        File file = project.file("$META_INF_GRADLE_PLUGINS/org.shipkit.plugin-name-sample.properties") << "implementation-class=org.shipkit.PluginNameSample"
        then:
        PluginDiscovery.getImplementationClass(file) == 'org.shipkit.PluginNameSample'
    }
}
