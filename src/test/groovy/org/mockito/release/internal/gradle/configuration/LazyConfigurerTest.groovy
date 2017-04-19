package org.mockito.release.internal.gradle.configuration

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

import static LazyConfigurer.getConfigurer

class LazyConfigurerTest extends Specification {

    def project1 = new ProjectBuilder().build()
    def project2 = new ProjectBuilder().withParent(project1).build()

    def "there is only one configurer"() {
        expect:
        getConfigurer(project1) == getConfigurer(project2)
    }
}
