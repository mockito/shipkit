package org.shipkit.gradle

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class BumpVersionFileTaskTest extends Specification {

    @Rule
    TemporaryFolder tmp = new TemporaryFolder()

    def task = new ProjectBuilder().build().tasks.create("bumpVersionFile", BumpVersionFileTask)

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
}
