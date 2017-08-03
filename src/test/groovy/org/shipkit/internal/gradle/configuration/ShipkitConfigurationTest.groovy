package org.shipkit.internal.gradle.configuration

import org.gradle.api.GradleException
import org.shipkit.gradle.configuration.ShipkitConfiguration
import org.shipkit.internal.gradle.util.team.TeamParser
import org.shipkit.internal.util.EnvVariables
import spock.lang.Specification
import spock.lang.Unroll

class ShipkitConfigurationTest extends Specification {

    def conf = new ShipkitConfiguration()

    def "default values"() {
        conf.team.developers.empty
        conf.team.contributors.empty
        conf.git.commitMessagePostfix == "[ci skip]"
        conf.releaseNotes.ignoreCommitsContaining == ["[ci skip]"]
    }

    def "validates team members"() {
        when:
        conf.team.developers = []
        conf.team.developers = ["foo:bar"]
        conf.team.developers = ["foo:bar", "x:y"]

        conf.team.contributors = []
        conf.team.contributors = ["foo:bar"]
        conf.team.contributors = ["foo:bar", "x:y"]

        then:
        noExceptionThrown()
    }

    def "fails when team members have wrong format"() {
        when: conf.team.developers = [""]
        then: thrown(TeamParser.InvalidInput.class)

        when: conf.team.contributors = ["ala:"]
        then: thrown(TeamParser.InvalidInput.class)
    }

    def "should use env variable for writeAuthToken when it's not set explicitly"() {
        given:
        def envVariables = Mock(EnvVariables)
        envVariables.getenv("GH_WRITE_TOKEN") >> "writeToken"
        conf = new ShipkitConfiguration(envVariables)

        expect:
        conf.gitHub.writeAuthToken == "writeToken"
    }

    def "should override env variable for writeAuthToken"() {
        given:
        def envVariables = Mock(EnvVariables)
        envVariables.getenv("GH_WRITE_TOKEN") >> "writeToken"
        conf = new ShipkitConfiguration(envVariables)
        conf.gitHub.writeAuthToken = "overriddenWriteToken"

        expect:
        conf.gitHub.writeAuthToken == "overriddenWriteToken"
    }

    @Unroll
    def "configures GitHub URL without ending slash when #url used"() {
        when:
        conf.gitHub.url = url
        conf.gitHub.apiUrl = url

        then:
        conf.gitHub.url == fixedUrl
        conf.gitHub.apiUrl == fixedUrl

        where:
        url                      | fixedUrl
        "https://github.com"     | "https://github.com"
        "https://github.com/"    | "https://github.com"
        "https://github.com////" | "https://github.com"
        "/"                      | ""
    }

    def "by default validates that settings are configured"() {
        when:
        conf.gitHub.repository

        then:
        thrown(GradleException)
    }

    def "offers a way to find out if settings are configured"() {
        expect:
        conf.lenient.gitHub.repository == null
    }
}
