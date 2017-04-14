package org.mockito.release.internal.gradle

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class ReleaseNotesPluginTest extends Specification {

    def project = new ProjectBuilder().build()

    def "applies cleanly"() {
        expect:
        project.plugins.apply("org.mockito.release-notes")
    }
}
