package org.shipkit.internal.gradle.plugin

import testutil.PluginSpecification


class PluginValidatorTest extends PluginSpecification {

    private static final String META_INF_GRADLE_PLUGINS = 'src/main/resources/META-INF/gradle-plugins'
    private static final String PLUGIN_PACKAGE = 'src/main/groovy/org/sipkit/gradle/'

    def "validate plugin properties files"() {
        given:
        project.file(META_INF_GRADLE_PLUGINS).mkdirs()
        project.file(PLUGIN_PACKAGE).mkdirs()
        def propertiesFile = project.file("$META_INF_GRADLE_PLUGINS/org.sipkit.test.properties") << "implementation-class=org.shipkit.gradle.TestPlugin"
        def pluginFile = project.file("$PLUGIN_PACKAGE/TestPlugin.java") << "TODO content"

        when:
        new PluginValidator().validate([pluginFile] as Set<File>, [propertiesFile] as Set<File>)
        then:
        true
    }

    def "validate plugin properties files (groovy plugin)"() {
        given:
        project.file(META_INF_GRADLE_PLUGINS).mkdirs()
        project.file(PLUGIN_PACKAGE).mkdirs()
        def propertiesFile = project.file("$META_INF_GRADLE_PLUGINS/org.sipkit.test.properties") << "implementation-class=org.shipkit.gradle.TestPlugin"
        def pluginFile = project.file("$PLUGIN_PACKAGE/TestPlugin.groovy") << "TODO content"

        when:
        new PluginValidator().validate([pluginFile] as Set<File>, [propertiesFile] as Set<File>)
        then:
        true
    }


    def "validate missing properties file"() {
        given:
        project.file(META_INF_GRADLE_PLUGINS).mkdirs()
        project.file(PLUGIN_PACKAGE).mkdirs()
        def pluginFile = project.file("$PLUGIN_PACKAGE/TestPlugin.java") << "TODO content"

        when:
        new PluginValidator().validate([pluginFile] as Set<File>, [] as Set<File>)
        then:
        RuntimeException ex = thrown()
        ex.message.contains 'no properties file found for plugin \'Test\''
    }

    def "validate missing properties file not matching"() {
        given:
        project.file(META_INF_GRADLE_PLUGINS).mkdirs()
        project.file(PLUGIN_PACKAGE).mkdirs()
        def propertiesFile = project.file("$META_INF_GRADLE_PLUGINS/org.sipkit.test2.properties") << "implementation-class=org.shipkit.gradle.TestPlugin"
        def pluginFile = project.file("$PLUGIN_PACKAGE/TestPlugin.java") << "TODO content"

        when:
        new PluginValidator().validate([pluginFile] as Set<File>, [propertiesFile] as Set<File>)
        then:
        RuntimeException ex = thrown()
        ex.message.contains 'no properties file found for plugin \'Test\''
    }

}
