package org.shipkit.internal.gradle

import org.gradle.api.GradleException
import org.shipkit.gradle.ReleaseConfiguration
import org.shipkit.internal.gradle.util.team.TeamParser
import spock.lang.Specification
import spock.lang.Unroll

class ReleaseConfigurationTest extends Specification {

    def conf = new ReleaseConfiguration()

    def "default values"() {
        conf.team.developers.empty
        conf.team.contributors.empty
        conf.git.commitMessagePostfix == "[ci skip]"
        conf.releaseNotes.ignoreCommitsContaining == ["[ci skip]"]
    }

    def "custom commitMessagePostfix"() {
        //TODO figure out a test that would validate all properties with reflection
        //rather than implement individual unit test for each property (getter and setter)
        conf.git.commitMessagePostfix = " by CI build 1234 [ci skip-release]"

        expect:
        conf.git.commitMessagePostfix ==  " by CI build 1234 [ci skip-release]"
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
