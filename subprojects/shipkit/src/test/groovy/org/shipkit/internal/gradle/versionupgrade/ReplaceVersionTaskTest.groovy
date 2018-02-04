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
        def configFile = tmp.newFile("build.gradle")

        configFile << "dependencies{ compile org.shipkit:shipkit:0.1.2 }"

        def tasksContainer = new ProjectBuilder().build().tasks
        def replaceVersionTask = tasksContainer.create("replaceVersion", ReplaceVersionTask)

        replaceVersionTask.dependencyGroup = "org.shipkit"
        replaceVersionTask.dependencyName = "shipkit"
        replaceVersionTask.newVersion = "0.2.3"
        replaceVersionTask.buildFile = configFile

        when:
        replaceVersionTask.replaceVersion()

        then:
        configFile.text == "dependencies{ compile org.shipkit:shipkit:0.2.3 }"
        replaceVersionTask.buildFileUpdated
    }

    def "sets buildFlagUpdated to false correctly"() {
        given:
        def configFile = tmp.newFile("build.gradle")

        configFile << "dependencies{ compile org.shipkit:shipkit:0.1.2 }"

        def tasksContainer = new ProjectBuilder().build().tasks
        def replaceVersionTask = tasksContainer.create("replaceVersion", ReplaceVersionTask)

        replaceVersionTask.dependencyGroup = "org.shipkit"
        replaceVersionTask.dependencyName = "shipkit"
        replaceVersionTask.newVersion = "0.1.2"
        replaceVersionTask.buildFile = configFile

        when:
        replaceVersionTask.replaceVersion()

        then:
        configFile.text == "dependencies{ compile org.shipkit:shipkit:0.1.2 }"
        !replaceVersionTask.buildFileUpdated
    }

    def "throws IllegalStateException when buildFileUpdated accessed before task is executed"() {
        given:
        def tasksContainer = new ProjectBuilder().build().tasks
        def replaceVersionTask = tasksContainer.create("replaceVersion", ReplaceVersionTask)

        when:
        replaceVersionTask.buildFileUpdated

        then:
        def ex = thrown(IllegalStateException)
        ex.message == "Property 'buildFileUpdated' should not be accessed before 'replaceVersion' task is executed."
    }
}
