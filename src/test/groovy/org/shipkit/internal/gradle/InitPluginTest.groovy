package org.shipkit.internal.gradle

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

import static org.shipkit.internal.gradle.InitPlugin.INIT_TRAVIS_TASK

class InitPluginTest extends Specification {

    def project = new ProjectBuilder().build()

    def "initializes .travis.yml file"() {
        project.plugins.apply(InitPlugin)
        assert !project.file(".travis.yml").exists()

        when:
        project.tasks[INIT_TRAVIS_TASK].execute()

        then:
        project.file(".travis.yml").isFile()
    }

    def "does not initialize .travis.yml file when already present"() {
        project.plugins.apply(InitPlugin)
        project.file(".travis.yml") << "foo"

        when:
        project.tasks[INIT_TRAVIS_TASK].execute()

        then:
        project.file(".travis.yml").text == "foo"
    }
}
