package org.shipkit.internal.gradle.bintray

import org.gradle.testfixtures.ProjectBuilder
import org.shipkit.internal.gradle.java.JavaBintrayPlugin
import spock.lang.Specification

class BintrayReleasePluginTest extends Specification {

    def project = new ProjectBuilder().build()

    def "applies"() {
        expect:
        project.plugins.apply(BintrayReleasePlugin)
        project.plugins.apply(JavaBintrayPlugin)
        project.evaluate()
    }
}
