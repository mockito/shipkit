package org.shipkit.internal.comparison.artifact

import org.apache.commons.lang.builder.EqualsBuilder
import org.gradle.api.Project
import org.gradle.api.plugins.PluginContainer
import org.shipkit.internal.gradle.bintray.ShipkitBintrayPlugin
import spock.lang.Specification

class DefaultArtifactUrlResolverFactoryTest extends Specification {

    def underTest = new DefaultArtifactUrlResolverFactory()
    def project = Mock(Project)

    def "returns null if no applicable DefaultArtifactUrlResolvers"() {
        given:
        project.plugins >> Mock(PluginContainer)

        expect:
        underTest.getDefaultResolver(project, "artifactName", "0.0.1") == null
    }

    def "returns Bintray resolver when BintrayPlugin is applied to the project"() {
        given:
        def pluginContainer = Mock(PluginContainer)
        project.plugins >> pluginContainer
        pluginContainer.hasPlugin(ShipkitBintrayPlugin) >> true

        when:
        def result = underTest.getDefaultResolver(project, "artifactName", "0.0.1")

        then:
        EqualsBuilder.reflectionEquals(result, new BintrayDefaultArtifactUrlResolver(project, "artifactName", "0.0.1"))
    }

}
