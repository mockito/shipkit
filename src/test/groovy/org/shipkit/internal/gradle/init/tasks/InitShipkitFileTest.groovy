package org.shipkit.internal.gradle.init.tasks

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.shipkit.internal.notes.vcs.GitOriginRepoProvider
import spock.lang.Specification

class InitShipkitFileTest extends Specification {

    @Rule TemporaryFolder tmp = new TemporaryFolder()

    def "does not modify shipkit file if it already exists"() {
        given:
        def shipkitFile = tmp.newFile()
        shipkitFile << "foo"

        when:
        InitShipkitFile.initShipkitFile(shipkitFile, tmp.root)

        then:
        shipkitFile.text == "foo"
    }

    def "uses fallback repo if git call fails"() {
        def shipkitFile = tmp.newFile()
        def repoProvider = Mock(GitOriginRepoProvider)
        repoProvider.getOriginGitRepo() >> { throw new RuntimeException() }

        expect:
        InitShipkitFile.getOriginGitRepo(repoProvider, shipkitFile) == "mockito/shipkit-example"
    }

    def "creates default shipkit file if it does not exist"() {
        given:
        def shipkitFile = tmp.newFile()
        def repoProvider = Mock(GitOriginRepoProvider)
        repoProvider.getOriginGitRepo() >> "mockito/mockito"

        when:
        InitShipkitFile.createShipkitFile(shipkitFile, repoProvider)

        then:
        shipkitFile.text ==
            """//This file was created automatically and is intended to be checked-in.
shipkit {
   gitHub.repository = \"mockito/mockito\"

   //TODO when you finish trying out Shipkit, use your own token below (http://link/needed)
   gitHub.readOnlyAuthToken = \"76826c9ec886612f504d12fd4268b16721c4f85d\"
}

allprojects {
   plugins.withId(\"org.shipkit.bintray\") {
       //TODO when you finish trying out Shipkit, use your own Bintray repository below (http://link/needed)
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
