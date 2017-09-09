package org.shipkit.internal.gradle.init

import org.gradle.testfixtures.ProjectBuilder
import org.shipkit.gradle.init.InitShipkitFileTask
import org.shipkit.gradle.init.InitVersioningTask
import org.shipkit.gradle.version.BumpVersionFileTask
import org.shipkit.internal.gradle.version.VersioningPlugin
import spock.lang.Specification

import static InitPlugin.INIT_TRAVIS_TASK

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

    def "initializes shipkit file task"() {
        when:
        project.plugins.apply(InitPlugin)

        then:
        InitShipkitFileTask task = project.tasks.findByName(InitPlugin.INIT_SHIPKIT_FILE_TASK)
        task.shipkitFile == project.file("gradle/shipkit.gradle")
    }

    def "initializes versioning file task"() {
        when:
        project.plugins.apply(InitPlugin)

        then:
        InitVersioningTask task = project.tasks.findByName(InitPlugin.INIT_VERSIONING_TASK)
        BumpVersionFileTask bumpTask = project.tasks[VersioningPlugin.BUMP_VERSION_FILE_TASK]
        bumpTask.versionFile == task.versionFile
    }
}
