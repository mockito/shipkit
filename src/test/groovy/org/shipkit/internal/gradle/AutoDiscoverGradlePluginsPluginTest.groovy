package org.shipkit.internal.gradle

import com.gradle.publish.PluginBundleExtension
import testutil.PluginSpecification

class AutoDiscoverGradlePluginsPluginTest extends PluginSpecification {

    public static final String META_INF_GRADLE_PLUGINS = 'src/main/resources/META-INF.gradle-plugins'

    def "Apply"() {
        expect:
        project.plugins.apply(AutoDiscoverGradlePluginsPlugin)
    }

    def "DiscoverGradlePluginPropertyFiles"() {
        given:
        project.file(META_INF_GRADLE_PLUGINS).mkdirs()
        project.file("$META_INF_GRADLE_PLUGINS/org.shipkit.test-plugin.properties") << "implementation-class=org.shipkit.TestPlugin"
        when:
        project.plugins.apply(AutoDiscoverGradlePluginsPlugin)
        project.plugins.apply("com.gradle.plugin-publish")
        then:
        PluginBundleExtension pluginBundleExtension = project.getExtensions().findByType(PluginBundleExtension.class);
        pluginBundleExtension.getPlugins()
        pluginBundleExtension.getPlugins().size() == 1
        pluginBundleExtension.plugins["testPlugin"]
        pluginBundleExtension.plugins["testPlugin"].id == 'org.shipkit.test-plugin'
    }

    def "GeneratePluginName"() {
        given:
        project.file(META_INF_GRADLE_PLUGINS).mkdirs()
        project.file("$META_INF_GRADLE_PLUGINS/plugin1.properties") << "implementation-class=org.shipkit.Plugin1"
        project.file("$META_INF_GRADLE_PLUGINS/org.shipkit.plugin-name-sample.properties") << "implementation-class=org.shipkit.PluginNameSample"
        project.file("another.properties") << "just another properties file"
        when:
        project.plugins.apply("com.gradle.plugin-publish")
        project.plugins.apply(AutoDiscoverGradlePluginsPlugin)
        then:
        PluginBundleExtension pluginBundleExtension = project.getExtensions().findByType(PluginBundleExtension.class);
        pluginBundleExtension.getPlugins()
        pluginBundleExtension.getPlugins().size() == 2
        pluginBundleExtension.plugins["plugin1"]
        pluginBundleExtension.plugins["plugin1"].id == 'plugin1'
        pluginBundleExtension.plugins["pluginNameSample"]
        pluginBundleExtension.plugins["pluginNameSample"].id == 'org.shipkit.plugin-name-sample'
    }
}
