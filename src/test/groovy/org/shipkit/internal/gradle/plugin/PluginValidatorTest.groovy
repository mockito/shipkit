package org.shipkit.internal.gradle.plugin

import testutil.PluginSpecification


class PluginValidatorTest extends PluginSpecification {

    private static final String META_INF_GRADLE_PLUGINS = 'src/main/resources/META-INF/gradle-plugins'
    private static final String PLUGIN_PACKAGE = 'src/main/groovy/org/sipkit/gradle/'

    def "validate plugin properties files"(propertiesFileName, className, extension) {
        given:
        project.file(META_INF_GRADLE_PLUGINS).mkdirs()
        project.file(PLUGIN_PACKAGE).mkdirs()
        def propertiesFile = project.file("$META_INF_GRADLE_PLUGINS/${propertiesFileName}.properties") << "implementation-class=org.shipkit.gradle.$className"
        def pluginFile = project.file("$PLUGIN_PACKAGE/${className}.${extension}") << "TODO content"

        when:
        new PluginValidator().validate([pluginFile] as Set<File>, [propertiesFile] as Set<File>)
        then:
        noExceptionThrown()

        where:
        propertiesFileName      | className          | extension
        'org.shipkit.test'      | 'TestPlugin'       | 'java'
        'org.shipkit.test'      | 'TestPlugin'       | 'groovy'
        'org.shipkit.my-sample' | 'MySamplePlugin'   | 'java'
        'org.shipkit.my-sample' | 'MySamplePlugin'   | 'groovy'
    }


    def "validate missing properties file"() {
        given:
        project.file(META_INF_GRADLE_PLUGINS).mkdirs()
        project.file(PLUGIN_PACKAGE).mkdirs()
        def pluginFile = project.file("$PLUGIN_PACKAGE/TestPlugin.java") << "TODO content"
        def pluginFile2 = project.file("$PLUGIN_PACKAGE/AnotherTestPlugin.java") << "TODO content"

        when:
        new PluginValidator().validate([pluginFile, pluginFile2] as Set<File>, [] as Set<File>)
        then:
        RuntimeException ex = thrown()
        ex.message.contains 'no properties file found for plugin(s):'
        ex.message.contains "\'Test\' ($pluginFile)"
        ex.message.contains "\'AnotherTest\' ($pluginFile2)"
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
        ex.message.contains 'no properties file found for plugin(s):'
        ex.message.contains "\'Test\' ($pluginFile)"
    }

}
