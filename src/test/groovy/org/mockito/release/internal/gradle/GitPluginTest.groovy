package org.mockito.release.internal.gradle

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification


class GitPluginTest extends Specification {

    def project = new ProjectBuilder().build()

    def setup() {
        project.plugins.apply(GitPlugin.class)
    }

    def "default commitMessagePostfix"() {
        expect:
        project.releasing.git.commitMessagePostfix == " [ci skip]"
    }

    def "custom commitMessagePostfix"() {
        def buildNo = 1234
        project.releasing.git.commitMessagePostfix = buildNo? " by CI build $buildNo [ci skip-release]" : " [ci skip-release]"

        expect:
        project.releasing.dryRun == true
        project.releasing.git.commitMessagePostfix ==  " by CI build 1234 [ci skip-release]"
    }
}
