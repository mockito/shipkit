package org.shipkit.internal.gradle.release

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class CiReleasePluginTest extends Specification {

    def project = new ProjectBuilder().build()

    def "applies"() {
        expect:
        project.plugins.apply(CiReleasePlugin)
    }
}
