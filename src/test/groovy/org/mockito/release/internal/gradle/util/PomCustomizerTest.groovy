package org.mockito.release.internal.gradle.util

import org.gradle.api.publish.maven.MavenPublication
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class PomCustomizerTest extends Specification {

    def project = new ProjectBuilder().build()

    def "registers xml action to customize pom"() {
        when:
        project.plugins.apply("java")
        project.plugins.apply("maven-publish")

        then:
        project.publishing.publications {
            mainJar(MavenPublication) {
                from project.components.java
                PomCustomizer.customizePom(project, null, it)
            }
        }
    }
}
