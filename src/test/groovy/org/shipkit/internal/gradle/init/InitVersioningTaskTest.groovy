package org.shipkit.internal.gradle.init

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.shipkit.gradle.init.InitVersioningTask
import spock.lang.Specification

class InitVersioningTaskTest extends Specification {

    @Rule
    TemporaryFolder tmp = new TemporaryFolder()

    def task = new ProjectBuilder().build().tasks.create("initVersioning", InitVersioningTask)

    def "does not modify version.properties if it already exists"() {
        given:
        def versionFileContent = "version=1.2.3\npreviousVersion=1.2.2"
        task.versionFile = tmp.newFile("version.properties")
        task.versionFile << versionFileContent

        when:
        task.initVersioning()

        then:
        task.versionFile.text == versionFileContent
    }

    def "should generate version properties and use 0.0.1 version if file doesn't exist and project.version unspecified"() {
        given:
        def versionFile = new File("${tmp.root.absolutePath}/version.properties")
        task.versionFile = versionFile

        when:
        task.initVersioning()

        then:
        task.versionFile.text ==
                "#Version of the produced binaries. This file is intended to be checked-in.\n" +
                "#It will be automatically bumped by release automation.\n" +
                "version=0.0.1\n"
    }

    def "should generate version properties and use project.version if file doesn't exist and project.version specified"() {
        given:
        def versionFile = new File("${tmp.root.absolutePath}/version.properties")
        task.versionFile = versionFile

        task.project.version("0.5.0")

        when:
        task.initVersioning()

        then:
        task.versionFile.text ==
                "#Version of the produced binaries. This file is intended to be checked-in.\n" +
                "#It will be automatically bumped by release automation.\n" +
                "version=0.5.0\n"
    }
}
