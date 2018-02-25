package org.shipkit.gradle.configuration

import org.gradle.api.GradleException
import org.shipkit.internal.gradle.util.team.TeamParser
import spock.lang.Specification

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
        when:
        conf.team.developers = [""]
        then:
        thrown(TeamParser.InvalidInput.class)

        when:
        conf.team.contributors = ["ala:"]
        then:
        thrown(TeamParser.InvalidInput.class)
    }

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
        conf.git.user = "foo"

        expect:
        conf.lenient.git.user == "foo"
        conf.lenient.gitHub.repository == null
    }
}
