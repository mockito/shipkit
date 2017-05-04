package org.mockito.release.internal.gradle.configuration

import org.gradle.api.Project
import spock.lang.Specification

import static org.mockito.release.internal.gradle.configuration.DeferredConfiguration.deferredConfiguration
import static org.mockito.release.internal.gradle.configuration.DeferredConfiguration.forceConfiguration


class DeferredConfigurationTest extends Specification {

    def project = Mock(Project)

    def "configures lazily"() {
        given:
        def action = { project.description = "foo" } as Runnable

        when:
        deferredConfiguration(project, action)

        then:
        0 * project.setDescription("foo")
        1 * project.afterEvaluate (_)

        when:
        forceConfiguration(project)

        then:
        1 * project.setDescription("foo")
    }
}
