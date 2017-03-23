package org.mockito.release.internal.gradle

import org.gradle.api.publish.maven.MavenPublication
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class DefaultPomPluginTest extends Specification {

    def project = new ProjectBuilder().build()

    def "adds extension method"() {
        when:
        project.plugins.apply("java")
        project.plugins.apply("maven-publish")
        project.plugins.apply("org.mockito.release-tools.pom")

        then:
        project.ext.pom_customizePom

        and:
        project.publishing.publications {
            mainJar(MavenPublication) {
                from project.components.java
                project.ext.pom_customizePom(project, it)
            }
        }
    }
}
