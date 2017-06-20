package org.shipkit.internal.gradle

import testutil.PluginSpecification

class PluginDiscoveryPluginTest extends PluginSpecification {

    private static final String META_INF_GRADLE_PLUGINS = 'src/main/resources/META-INF.gradle-plugins'

    def "apply"() {
        expect:
        project.plugins.apply(PluginDiscoveryPlugin)
    }

    def "discover gradle plugin properties files"() {
        given:
        project.file(META_INF_GRADLE_PLUGINS).mkdirs()
        project.file("$META_INF_GRADLE_PLUGINS/plugin1.properties") << "implementation-class=org.shipkit.Plugin1"
        project.file("$META_INF_GRADLE_PLUGINS/org.shipkit.plugin-name-sample.properties") << "implementation-class=org.shipkit.PluginNameSample"
        project.file("another.properties") << "just another properties file"
        when:
        project.plugins.apply("com.gradle.plugin-publish")
        project.plugins.apply(PluginDiscoveryPlugin)
        then:
        project.pluginBundle.plugins
        project.pluginBundle.plugins.size() == 2
        project.pluginBundle.plugins["plugin1"]
        project.pluginBundle.plugins["plugin1"].id == 'plugin1'
        project.pluginBundle.plugins["pluginNameSample"]
        project.pluginBundle.plugins["pluginNameSample"].id == 'org.shipkit.plugin-name-sample'
    }

    def "generate plugin name"() {
        expect:
        PluginDiscoveryPlugin.generatePluginName(input) == expected

        where:
        input                                       | expected
        'plugin.properties'                         | 'plugin'
        'com.shipkit.base-java-library.properties'  | 'baseJavaLibrary'
        'com.shipkit.versioning.properties'         | 'versioning'
    }
}
