package org.mockito.release.internal.gradle

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class AutoVersioningPluginTest extends Specification {

    def project = new ProjectBuilder().build()

    def "applies"() {
        expect:
        project.plugins.apply(AutoVersioningPlugin)
    }

}
