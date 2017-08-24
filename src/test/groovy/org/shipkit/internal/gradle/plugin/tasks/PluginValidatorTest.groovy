package org.shipkit.internal.gradle.plugin.tasks

import testutil.PluginSpecification

class PluginValidatorTest extends PluginSpecification {

    private static final String META_INF_GRADLE_PLUGINS = 'src/main/resources/META-INF/gradle-plugins'
    private static final String PLUGIN_PACKAGE = 'src/main/groovy/org/shipkit/internal/gradle'

    def "validate plugin properties files"(propertiesFileName, className, extension) {
        given:
        project.file(META_INF_GRADLE_PLUGINS).mkdirs()
        project.file(PLUGIN_PACKAGE).mkdirs()
        Set propertiesFiles = [project.file("$META_INF_GRADLE_PLUGINS/${propertiesFileName}.properties") << "implementation-class=org.shipkit.internal.gradle.$className"]
        project.file("$PLUGIN_PACKAGE/${className}.${extension}") << "some content"

        when:
        new PluginValidator([project.file('src/main/groovy')] as Set).validate(propertiesFiles)
        then:
        noExceptionThrown()

        where:
        propertiesFileName                      | className                         | extension
        'org.shipkit.bintray'                   | 'ShipkitBintrayPlugin'            | 'java'
        'org.shipkit.bintray'                   | 'ShipkitBintrayPlugin'            | 'groovy'
        'org.shipkit.github-pom-contributors'   | 'GitHubPomContributorsPlugin'     | 'java'
        'org.shipkit.github-pom-contributors'   | 'GitHubPomContributorsPlugin'     | 'groovy'
        'org.shipkit.bintray'                   | 'ShipkitBintrayPlugin'            | 'groovy'
        'org.shipkit.gradle-plugin'             | 'ShipkitGradlePlugin'             | 'java'
        'org.shipkit.gradle-plugin'             | 'ShipkitGradlePlugin'             | 'groovy'
    }



    /*def "validate wrong classname"() {
        given:
        project.file(META_INF_GRADLE_PLUGINS).mkdirs()
        project.file(PLUGIN_PACKAGE).mkdirs()
        def pluginFile = project.file("$PLUGIN_PACKAGE/TestPlugin.java") << "some content"
        def pluginFile2 = project.file("$PLUGIN_PACKAGE/AnotherTestPlugin.java") << "some content"
        Set pluginFiles = [pluginFile, pluginFile2]
        Set propertiesFiles = []

        when:
        new PluginValidator([project.file('src/main/groovy')] as Set).validate(propertiesFiles)
        then:
        RuntimeException ex = thrown()
        ex.message.contains 'no properties file found for plugin(s):'
        ex.message.contains "\'Test\' ($pluginFile)"
        ex.message.contains "\'AnotherTest\' ($pluginFile2)"
    }

    def "validate missing implementationClass"(propertiesFileName, className, extension) {

        given:
        project.file(META_INF_GRADLE_PLUGINS).mkdirs()
        project.file(PLUGIN_PACKAGE).mkdirs()
        Set propertiesFiles = [project.file("$META_INF_GRADLE_PLUGINS/${propertiesFileName}.properties") << "implementation-class=org.shipkit.internal.gradle.$className"]

        when:
        new PluginValidator([project.file('src/main/groovy')] as Set).validate(propertiesFiles)
        then:
        RuntimeException ex = thrown()
        ex.message == "Implemntation class org.shipkit.internal.gradle.${className} does not exist!"

        where:
        propertiesFileName                      | className                         | extension
        'org.shipkit.bintray'                   | 'ShipkitBintrayPlugin'            | 'java'
        'org.shipkit.bintray'                   | 'ShipkitBintrayPlugin'            | 'groovy'

    }*/

}
