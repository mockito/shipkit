package org.shipkit.internal.comparison.artifact

import org.gradle.api.Project
import spock.lang.Specification

class GradlePluginArtifactUrlResolverTest extends Specification {

    GradlePluginArtifactUrlResolver underTest
    def project = Mock(Project)

    def "concatenates default url correctly"() {
        given:
        underTest = new GradlePluginArtifactUrlResolver("test.group", "api", "0.0.1")

        when:
        def result = underTest.getDefaultUrl("-sources.jar")

        then:
        result == "https://plugins.gradle.org/m2/test/group/api/0.0.1/api-0.0.1-sources.jar"
    }
}
