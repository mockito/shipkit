package org.shipkit.internal.gradle.plugin

import testutil.PluginSpecification

class PluginDiscoveryPluginTest extends PluginSpecification {

    static final String META_INF_GRADLE_PLUGINS = 'src/main/resources/META-INF/gradle-plugins'

    def "apply"() {
        expect:
        project.plugins.apply(PluginDiscoveryPlugin)
    }

    def "discover gradle plugin properties files"() {
        given:
        project.file(META_INF_GRADLE_PLUGINS).mkdirs()
        project.file("$META_INF_GRADLE_PLUGINS/plugin1.properties") << "implementation-class=org.shipkit.Plugin1"
        project.file("$META_INF_GRADLE_PLUGINS/org.shipkit.plugin-name-sample.properties") << "implementation-class=org.shipkit.PluginNameSample"
        project.file("src/main/resources/template.travis.yml") << "another file in src/main/resources"
        project.file("src/main/resources/test.properties") << "properties file in src/main/resources"
        project.file("another.properties") << "just another properties file"
        when:
        project.plugins.apply(PluginDiscoveryPlugin)
        project.tasks[PluginDiscoveryPlugin.DISCOVER_PLUGINS].execute()
        then:
        project.pluginBundle.plugins
        project.pluginBundle.plugins.size() == 2
        project.pluginBundle.plugins["plugin1"]
        project.pluginBundle.plugins["plugin1"].id == 'plugin1'
        project.pluginBundle.plugins["pluginNameSample"]
        project.pluginBundle.plugins["pluginNameSample"].id == 'org.shipkit.plugin-name-sample'
    }
}
