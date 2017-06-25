package org.shipkit.internal.gradle

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class PomContributorsPluginTest extends Specification {

    def project = new ProjectBuilder().build()

    def "applies"() {
        expect:
        project.plugins.apply(PomContributorsPlugin)
        project.plugins.apply(BaseJavaLibraryPlugin)
    }
}
