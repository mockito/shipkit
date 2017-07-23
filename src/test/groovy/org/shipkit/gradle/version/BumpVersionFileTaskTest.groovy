package org.shipkit.gradle.version

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.shipkit.gradle.version.BumpVersionFileTask
import org.shipkit.internal.version.Version
import spock.lang.Specification

class BumpVersionFileTaskTest extends Specification {

    @Rule
    TemporaryFolder tmp = new TemporaryFolder()

    def project = new ProjectBuilder().build()
    def task = project.tasks.create("bumpVersionFile", BumpVersionFileTask)

    def "bumps version if version.properties file exists"() {
        given:
        def versionFile = tmp.newFile("version.properties")
        versionFile << "version=1.0.1\npreviousVersion=1.0.0"
        task.setVersionFile(versionFile)

        when:
        def result = task.bumpVersionFile()

        then:
        result.version == "1.0.2"
        result.previousVersion == "1.0.1"
    }

    def "shows informative message"() {
        def versionFile = project.file("version.properties")
        versionFile << "version=1.0.1\npreviousVersion=1.0.0"
        task.versionFile = versionFile
        def info = Version.versionInfo(versionFile)

        expect:
        BumpVersionFileTask.versionMessage(task, info) == """:bumpVersionFile - updated version file 'version.properties'
  - new version: 1.0.1
  - previous version: 1.0.0"""
    }
}
