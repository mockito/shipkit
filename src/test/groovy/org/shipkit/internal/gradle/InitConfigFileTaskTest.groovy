package org.shipkit.internal.gradle

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.shipkit.internal.notes.vcs.GitOriginRepoProvider
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

    def "uses fallback repo if call to gitOriginRepoProvider fails"() {
        given:
        def configFile = new File("${tmp.root.absolutePath}/shipkit.gradle")
        task.configFile = configFile
        def gitOriginRepoProvider = Mock(GitOriginRepoProvider)
        task.setGitOriginRepoProvider(gitOriginRepoProvider)
        gitOriginRepoProvider.getOriginGitRepo() >> {throw new RuntimeException()}

        when:
        task.initShipkitConfigFile()

        then:
        task.configFile.text.contains('gitHub.repository = "mockito/shipkit-example"')
    }

    def "creates default shipkit config file if it does not exist"() {
        given:
        def configFile = new File("${tmp.root.absolutePath}/shipkit.gradle")
        task.configFile = configFile
        def gitOriginRepoProvider = Mock(GitOriginRepoProvider)
        task.setGitOriginRepoProvider(gitOriginRepoProvider)
        gitOriginRepoProvider.getOriginGitRepo() >> "mockito/mockito"

        when:
        task.initShipkitConfigFile()

        then:
        task.configFile.text ==
                """//This file was created automatically and is intended to be checked-in.
shipkit {
   gitHub.repository = \"mockito/mockito\"
   gitHub.readOnlyAuthToken = \"76826c9ec886612f504d12fd4268b16721c4f85d\"
   gitHub.writeAuthUser = \"shipkit-org\"
}

allprojects {
   plugins.withId(\"org.shipkit.bintray\") {
       bintray {
           key = '7ea297848ca948adb7d3ee92a83292112d7ae989'
           pkg {
               repo = 'bootstrap'
               user = 'shipkit-bootstrap-bot'
               userOrg = 'shipkit-bootstrap'
               name = 'maven'
               licenses = ['MIT']
               labels = ['continuous delivery', 'release automation', 'shipkit']
           }
       }
   }
}
"""
    }
}
