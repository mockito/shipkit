package org.shipkit.internal.gradle.plugin

import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskExecutionException
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

    def "validate fails"(propertiesFileName, className, extension, generateImplClass, errorMessage) {
        given:
        String pluginPackage = "src/main/$extension/org/shipkit/internal/gradle"

        project.file(META_INF_GRADLE_PLUGINS).mkdirs()
        project.file(pluginPackage).mkdirs()
        project.file("$META_INF_GRADLE_PLUGINS/${propertiesFileName}.properties") << "implementation-class=org.shipkit.internal.gradle.$className"
        if(generateImplClass) {
            project.file("$pluginPackage/${className}.${extension}") << "some content"
        }

        when:
        project.plugins.apply(extension)
        project.plugins.apply(PluginValidationPlugin)
        project.tasks[PluginValidationPlugin.VALIDATE_PLUGINS].execute()
        then:
        TaskExecutionException ex = thrown()
        GradleException cause = ex.getCause()
        cause.message.contains 'plugin validation failed for plugin(s):'
        String expectedMessagePrefix = "Implementation class org.shipkit.internal.gradle.$className does not "
        cause.message.contains expectedMessagePrefix + errorMessage

        where:
        propertiesFileName      | className                | extension | generateImplClass  | errorMessage
        'org.shipkit.bintray'   | 'AnotherShipkitPlugin'   | 'java'    | true               | "match one of the acceptable names [BintrayPlugin, ShipkitBintrayPlugin, OrgShipkitBintrayPlugin]"
        'org.shipkit.bintray'   | 'ShipkitBintrayPlugin'   | 'java'    | false              | "exist!"
    }
}
