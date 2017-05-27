package org.mockito.release.internal.gradle

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class InitConfigFileTaskTest extends Specification {

    @Rule
    TemporaryFolder tmp = new TemporaryFolder()

    def task = new ProjectBuilder().build().tasks.create("initConfigFile", InitConfigFileTask)

    def "does not modify shipkit config file if it already exists"() {
        given:
        def configFileContent = "shipkit{}"
        task.configFile = tmp.newFile("shipkit.gradle")
        task.configFile << configFileContent

        when:
        task.initShipkitConfigFile()

        then:
        task.configFile.text == configFileContent
    }

    def "creates default shipkit config file if it does not exist"() {
        given:
        def configFile = new File("${tmp.root.absolutePath}/shipkit.gradle")
        task.configFile = configFile

        when:
        task.initShipkitConfigFile()

        then:
        task.configFile.text ==
                """//This file was created automatically and is intended to be checked-in.
releasing {
   gitHub.repository = \"mockito/mockito-release-tools-example\"
   gitHub.readOnlyAuthToken = \"e7fe8fcfd6ffedac384c8c4c71b2a48e646ed1ab\"
   gitHub.writeAuthUser = \"shipkit\"
}

allprojects {
   plugins.withId(\"org.mockito.mockito-release-tools.bintray\") {
       bintray {
           pkg {
               repo = 'examples'
               user = 'szczepiq'
               userOrg = 'shipkit'
               name = 'basic'
               licenses = ['MIT']
               labels = ['continuous delivery', 'release automation', 'mockito']
           }
       }
   }
}
"""
    }
}
