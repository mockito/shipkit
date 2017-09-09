package org.shipkit.internal.gradle.java

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class JavaLibraryPluginTest extends Specification {

    def project = new ProjectBuilder().build()

    def "applies"() {
        expect:
        project.plugins.apply(JavaLibraryPlugin)
    }
}
