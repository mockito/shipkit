package org.shipkit.internal.gradle.versionupgrade

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.shipkit.internal.gradle.VersionUpgradeConsumerExtension
import spock.lang.Specification

class ReplaceVersionTaskTest extends Specification {

    @Rule
    TemporaryFolder tmp = new TemporaryFolder()

    def "replaces version"() {
        given:
        def configFile = tmp.newFile("build.gradle")

        configFile << "dependencies{ compile org.shipkit:shipkit:0.1.2 }"

        def versionUpgrade = new VersionUpgradeConsumerExtension(
            dependencyGroup: "org.shipkit",
            dependencyName: "shipkit",
            newVersion: "0.2.3",
            buildFile: configFile
        )
        def tasksContainer = new ProjectBuilder().build().tasks
        def replaceVersionTask = tasksContainer.create("replaceVersion", ReplaceVersionTask)

        replaceVersionTask.versionUpgrade = versionUpgrade

        when:
        replaceVersionTask.replaceVersion()

        then:
        configFile.text == "dependencies{ compile org.shipkit:shipkit:0.2.3 }"
    }

}
