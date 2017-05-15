package org.mockito.release.internal.gradle

import org.mockito.release.gradle.ReleaseConfiguration
import org.mockito.release.internal.gradle.util.team.TeamParser
import spock.lang.Specification

import static java.util.Arrays.asList

class ReleaseConfigurationTest extends Specification {

    def conf = new ReleaseConfiguration()

    def "default values"() {
        conf.team.developers.empty
        conf.team.contributors.empty
        conf.git.commitMessagePostfix == "[ci skip]"
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

        conf.team.addAllContributors([])
        conf.team.addAllContributors(["foo:bar"])
        conf.team.addAllContributors(["foo:bar", "x:y"])

        then:
        noExceptionThrown()
    }

    def "fails when team members have wrong format"() {
        when: conf.team.developers = [""]
        then: thrown(TeamParser.InvalidInput.class)

        when: conf.team.addAllContributors(["ala:"])
        then: thrown(TeamParser.InvalidInput.class)
    }

    def "contributors list should return always original reference because of global state"() {
        given:
        def contributors = conf.getTeam().getContributors()
        def newContributors = asList("foo:bar", "fiz:buz")

        when:
        conf.getTeam().addAllContributors(newContributors)

        then:
        conf.getTeam().getContributors() == contributors    // check is the same reference
        conf.getTeam().getContributors().containsAll(asList("foo:bar", "fiz:buz"))
    }
}
