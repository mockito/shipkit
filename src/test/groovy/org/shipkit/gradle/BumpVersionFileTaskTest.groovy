package org.shipkit.gradle

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class BumpVersionFileTaskTest extends Specification {

    @Rule
    TemporaryFolder tmp = new TemporaryFolder()

    def task = new ProjectBuilder().build().tasks.create("bumpVersionFile", BumpVersionFileTask)

    def "fails if version.properties file doesn't exist"() {
        given:
        task.setVersionFile(new File("${tmp.root.absolutePath}/version.properties"))

        when:
        task.bumpVersionFile()

        then:
        def ex = thrown(IllegalStateException)
        ex.message == "Cannot bump version because 'version.properties' file doesn't exist. Use 'initShipkit' task to create it."
    }

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
