package org.shipkit.internal.gradle.travis

import org.shipkit.gradle.configuration.ShipkitConfiguration
import spock.lang.Specification

class TravisUtilsTest extends Specification {

    def "should build travis url with [ci skip]"() {
        given:
        ShipkitConfiguration shipkitConfiguration = Mock(ShipkitConfiguration)
        ShipkitConfiguration.GitHub gitHub = Mock(ShipkitConfiguration.GitHub)
        shipkitConfiguration.getGitHub() >> gitHub
        1 * gitHub.getRepository() >> "mockito/shipkit"
        0 * _
        when:
        def url = TravisUtils.generateCommitMessage(shipkitConfiguration, "original [ci skip]", "123")
        then:
        url == "original. CI job: https://travis-ci.org/mockito/shipkit/builds/123 [ci skip]"
    }

    def "should build travis url without [ci skip]"() {
        given:
        ShipkitConfiguration shipkitConfiguration = Mock(ShipkitConfiguration)
        ShipkitConfiguration.GitHub gitHub = Mock(ShipkitConfiguration.GitHub)
        shipkitConfiguration.getGitHub() >> gitHub
        1 * gitHub.getRepository() >> "mockito/shipkit"
        0 * _
        when:
        def url = TravisUtils.generateCommitMessage(shipkitConfiguration, "original", "123")
        then:
        url == "original. CI job: https://travis-ci.org/mockito/shipkit/builds/123"
    }
}
