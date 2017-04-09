package org.mockito.release.internal.gradle

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class DefaultContributorsPluginTest extends Specification {

    def project = new ProjectBuilder().build()

    def "applies"() {

        expect:
        project.plugins.apply("org.mockito.release-tools.contributors")
    }
}
