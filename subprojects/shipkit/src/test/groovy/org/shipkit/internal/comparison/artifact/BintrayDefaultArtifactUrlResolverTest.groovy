package org.shipkit.internal.comparison.artifact

import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import spock.lang.Specification

class BintrayDefaultArtifactUrlResolverTest extends Specification {

    BintrayDefaultArtifactUrlResolver underTest
    Project project = Mock(Project)
    BintrayExtension bintray = Mock(BintrayExtension)

    def setup() {
        ExtensionContainer extensionContainer = Mock(ExtensionContainer)
        BintrayExtension.PackageConfig pkg = Mock(BintrayExtension.PackageConfig)

        project.getExtensions() >> extensionContainer
        extensionContainer.getByType(BintrayExtension.class) >> bintray
        bintray.pkg >> pkg
    }

    def "concatenates default url with userOrg correctly"() {
        given:
        underTest = new BintrayDefaultArtifactUrlResolver(project, "api", "0.0.1")
        project.getGroup() >> "mockito"
        bintray.pkg.userOrg >> "shipkit-org"
        bintray.pkg.repo >> "examples"
        when:
        def result = underTest.getDefaultUrl("-sources.jar")
        then:
        result == "https://bintray.com/shipkit-org/examples/download_file?file_path=mockito/api/0.0.1/api-0.0.1-sources.jar"
    }

    def "concatenates default url with fallback to user when pkg.userOrg is null"() {
        given:
        underTest = new BintrayDefaultArtifactUrlResolver(project, "api", "0.0.1")
        project.getGroup() >> "mockito"
        bintray.user >> "shipkit-user"
        bintray.pkg.repo >> "examples"
        when:
        def result = underTest.getDefaultUrl("-sources.jar")
        then:
        result == "https://bintray.com/shipkit-user/examples/download_file?file_path=mockito/api/0.0.1/api-0.0.1-sources.jar"
    }
}
