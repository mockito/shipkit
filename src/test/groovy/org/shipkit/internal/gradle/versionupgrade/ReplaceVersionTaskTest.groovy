package org.shipkit.internal.gradle.versionupgrade

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class ReplaceVersionTaskTest extends Specification {

    @Rule
    TemporaryFolder tmp = new TemporaryFolder()

    def "replaces version"() {
        given:
        def tasksContainer = new ProjectBuilder().build().tasks
        def replaceVersionTask = tasksContainer.create("replaceVersion", ReplaceVersionTask)
        def configFile = tmp.newFile("build.gradle")

        configFile << "dependencies{ compile org.shipkit:shipkit:0.1.2 }"

        replaceVersionTask.buildFile = configFile
        replaceVersionTask.newVersion = "0.2.3"
        replaceVersionTask.dependencyPattern = "org.shipkit:shipkit:{VERSION}"

        when:
        replaceVersionTask.replaceVersion()

        then:
        configFile.text == "dependencies{ compile org.shipkit:shipkit:0.2.3 }"
    }

}
