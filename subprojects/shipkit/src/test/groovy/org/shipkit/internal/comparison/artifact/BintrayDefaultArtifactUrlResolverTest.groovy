package org.shipkit.internal.comparison.artifact

import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import spock.lang.Specification

class BintrayDefaultArtifactUrlResolverTest extends Specification {

    BintrayDefaultArtifactUrlResolver underTest
    Project project = Mock(Project)
    ExtensionContainer extensionContainer = Mock(ExtensionContainer)
    BintrayExtension extension = Mock(BintrayExtension)
    BintrayExtension.PackageConfig pkg = Mock(BintrayExtension.PackageConfig)

    def setup() {
        project.getExtensions() >> extensionContainer
        extensionContainer.getByType(BintrayExtension.class) >> extension
        extension.pkg >> pkg
    }

    def "concatenates default url with userOrg correctly"() {
        given:
        underTest = new BintrayDefaultArtifactUrlResolver(project, "api", "0.0.1")
        project.getGroup() >> "mockito"
        pkg.userOrg >> "shipkit"
        pkg.repo >> "examples"
        when:
        def result = underTest.getDefaultUrl("-sources.jar")
        then:
        result == "https://bintray.com/shipkit/examples/download_file?file_path=mockito/api/0.0.1/api-0.0.1-sources.jar"
    }

    def "concatenates default url with fallback to user correctly"() {
        given:
        underTest = new BintrayDefaultArtifactUrlResolver(project, "api", "0.0.1")
        project.getGroup() >> "mockito"
        extension.user >> "shipkit"
        pkg.repo >> "examples"
        when:
        def result = underTest.getDefaultUrl("-sources.jar")
        then:
        result == "https://bintray.com/shipkit/examples/download_file?file_path=mockito/api/0.0.1/api-0.0.1-sources.jar"
    }
}
