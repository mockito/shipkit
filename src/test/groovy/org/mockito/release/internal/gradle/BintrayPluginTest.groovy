package org.mockito.release.internal.gradle

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class BintrayPluginTest extends Specification {

    def project = new ProjectBuilder().build()

    def "configures extension"() {
        project.version = "1.0"
        project.plugins.apply("org.mockito.mockito-release-tools.bintray")

        expect:
        project.bintray.pkg.version.vcsTag == "v1.0"
    }

    def "deferred configuration"() {
        project.version = "1.0"
        project.description = "some proj"
        project.plugins.apply("org.mockito.mockito-release-tools.bintray")

        project.releasing.dryRun = true
        project.releasing.bintray.apiKey = '!@#'
        project.releasing.gitHub.repository = 'repo'
        project.bintray.user = 'szczepiq'

        when:
        project.evaluate()

        then:
        project.bintray.dryRun == true
        project.bintray.key == '!@#'
        project.bintray.pkg.vcsUrl == "https://github.com/repo.git"
        project.bintray.pkg.issueTrackerUrl == "https://github.com/repo/issues"
        project.bintray.pkg.websiteUrl == "https://github.com/repo"
        project.bintray.pkg.desc == "some proj"
    }

    def "deferred configuration honors user settings"() {
        project.version = "1.0"
        project.description = "some proj"
        project.plugins.apply("org.mockito.mockito-release-tools.bintray")

        project.releasing.dryRun = true
        project.releasing.bintray.apiKey = '!@#'
        project.releasing.gitHub.repository = 'repo'

        project.bintray.dryRun = false //this one is not honored at the moment, we're ok with this
        project.bintray.user = 'szczepiq'
        project.bintray.key = 'xyz'
        project.bintray.pkg.vcsUrl = "vcs"
        project.bintray.pkg.issueTrackerUrl = "issueTracker"
        project.bintray.pkg.websiteUrl = "website"
        project.bintray.pkg.desc = "my desc"

        when:
        project.evaluate()

        then:
        project.bintray.dryRun == true
        project.bintray.key == 'xyz'
        project.bintray.pkg.vcsUrl == "vcs"
        project.bintray.pkg.issueTrackerUrl == "issueTracker"
        project.bintray.pkg.websiteUrl == "website"
        project.bintray.pkg.desc == "my desc"
    }
}
