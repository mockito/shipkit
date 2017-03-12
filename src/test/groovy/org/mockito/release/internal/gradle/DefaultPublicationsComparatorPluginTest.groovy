package org.mockito.release.internal.gradle

import org.gradle.api.publish.maven.MavenPublication
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

import static org.mockito.release.internal.gradle.DefaultPublicationsComparatorPlugin.configureTasks

class DefaultPublicationsComparatorPluginTest extends Specification {

    def project = new ProjectBuilder().build()

    def "applies to project with maven publication"() {
        project.plugins.apply("java")
        project.plugins.apply("maven-publish")
        project.plugins.apply(DefaultPublicationsComparatorPlugin)

        project.publishing {
            publications {
                MainJar(MavenPublication) {
                    from project.components.java
                }
            }
        }

        when:
        configureTasks(project)

        then:
        project.tasks['comparePomsForMainJar']
    }

    def "applies to plain java project safely"() {
        project.plugins.apply("java")
        project.plugins.apply(DefaultPublicationsComparatorPlugin)

        expect:
        configureTasks(project)
    }
}
