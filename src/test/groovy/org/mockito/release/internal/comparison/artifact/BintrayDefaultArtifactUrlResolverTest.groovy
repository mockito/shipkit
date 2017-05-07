package org.mockito.release.internal.comparison.artifact

import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import spock.lang.Specification

class BintrayDefaultArtifactUrlResolverTest extends Specification {

    BintrayDefaultArtifactUrlResolver underTest
    Project project = Mock(Project)

    def "concatenates default url correctly"() {
        given:
        underTest = new BintrayDefaultArtifactUrlResolver(project, "api", "0.0.1")
        project.getGroup() >> "mockito"
        def pkg = mockBintrayPkg()
        pkg.userOrg >> "shipkit"
        pkg.repo >> "examples"
        when:
        def result = underTest.getDefaultUrl("-sources.jar")
        then:
        result == "https://bintray.com/shipkit/examples/download_file?file_path=mockito/api/0.0.1/api-0.0.1-sources.jar"
    }

    private BintrayExtension.PackageConfig mockBintrayPkg() {
        def extensionContainer = Mock(ExtensionContainer)
        project.getExtensions() >> extensionContainer
        def extension = Mock(BintrayExtension)
        extensionContainer.getByType(BintrayExtension.class) >> extension
        def pkg = Mock(BintrayExtension.PackageConfig)
        extension.pkg >> pkg
        pkg
    }
}
