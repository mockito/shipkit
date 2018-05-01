package org.shipkit.internal.gradle.util

import spock.lang.Specification

import org.shipkit.gradle.configuration.ShipkitConfiguration

class TravisCommitMessageUtilsTest extends Specification {

    def "should build travis url with [ci skip]"() {
        given:
        ShipkitConfiguration shipkitConfiguration = Mock(ShipkitConfiguration)
        ShipkitConfiguration.GitHub gitHub = Mock(ShipkitConfiguration.GitHub)
        ShipkitConfiguration.Git git = Mock(ShipkitConfiguration.Git)
        shipkitConfiguration.getGitHub() >> gitHub
        shipkitConfiguration.getGit() >> git
        1 * git.commitMessagePostfix >> "[ci skip]"
        1 * gitHub.getRepository() >> "mockito/shipkit"
        0 * _
        when:
        def url = TravisCommitMessageUtils.decorateCommitMessagePostfix(shipkitConfiguration, "123")
        then:
        url == "CI job: https://travis-ci.org/mockito/shipkit/builds/123 [ci skip]"
    }

    def "should build postfix without travis url if blank build number"() {
        given:
        ShipkitConfiguration shipkitConfiguration = Mock(ShipkitConfiguration)
        ShipkitConfiguration.GitHub gitHub = Mock(ShipkitConfiguration.GitHub)
        ShipkitConfiguration.Git git = Mock(ShipkitConfiguration.Git)
        shipkitConfiguration.getGitHub() >> gitHub
        shipkitConfiguration.getGit() >> git

        1 * git.commitMessagePostfix >> "[ci skip]"
        0 * _
        when:
        def url = TravisCommitMessageUtils.decorateCommitMessagePostfix(shipkitConfiguration, "")
        then:
        url == "[ci skip]"
    }
}
