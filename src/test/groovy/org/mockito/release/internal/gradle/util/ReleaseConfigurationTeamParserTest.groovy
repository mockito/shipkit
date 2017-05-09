package org.mockito.release.internal.gradle.util

import spock.lang.Specification
import spock.lang.Unroll

import static org.mockito.release.internal.gradle.util.ReleaseConfigurationTeamParser.parsePerson
import static org.mockito.release.internal.gradle.util.ReleaseConfigurationTeamParser.validateTeamMembers

class ReleaseConfigurationTeamParserTest extends Specification {

    def "parses person"() {
        when:
        def p = parsePerson("szczepiq:Szczepan Faber")

        then:
        p.name == 'Szczepan Faber'
        p.gitHubUser == 'szczepiq'
    }

    def "validates persons"() {
        validateTeamMembers(["foo:bar", "a:b"])
        when:
        validateTeamMembers(["foo:bar", "a:"])
        then:
        thrown(ReleaseConfigurationTeamParser.InvalidInput)
    }

    @Unroll
    def "invalid input '#input'"() {
        when:
        parsePerson(input)
        then:
        thrown(ReleaseConfigurationTeamParser.InvalidInput)
        where:
        input << ["", "  ", ":", "a:", ":b", "a:b:c"]
    }
}
