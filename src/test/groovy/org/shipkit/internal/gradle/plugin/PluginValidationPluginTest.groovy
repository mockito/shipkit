package org.shipkit.internal.gradle.plugin

import testutil.PluginSpecification

class PluginValidationPluginTest extends PluginSpecification {

    def "apply"() {
        expect:
        project.plugins.apply(PluginValidationPlugin)
    }

    private static final String META_INF_GRADLE_PLUGINS = 'src/main/resources/META-INF/gradle-plugins'

    def "validate plugin properties files"(propertiesFileName, className, extension) {
        given:
        String pluginPackage = "src/main/$extension/org/shipkit/internal/gradle"

        project.file(META_INF_GRADLE_PLUGINS).mkdirs()
        project.file(pluginPackage).mkdirs()
        project.file("$META_INF_GRADLE_PLUGINS/${propertiesFileName}.properties") << "implementation-class=org.shipkit.internal.gradle.$className"
        project.file("$pluginPackage/${className}.${extension}") << "some content"

        when:
        project.plugins.apply(extension)
        project.plugins.apply(PluginValidationPlugin)
        project.tasks[PluginValidationPlugin.VALIDATE_PLUGINS].execute()
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
}
