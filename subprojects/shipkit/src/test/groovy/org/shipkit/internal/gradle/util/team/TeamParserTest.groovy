package org.shipkit.internal.gradle.util.team

import spock.lang.Specification
import spock.lang.Unroll

import static org.shipkit.internal.gradle.util.team.TeamParser.parsePerson
import static org.shipkit.internal.gradle.util.team.TeamParser.validateTeamMembers

class TeamParserTest extends Specification {

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
        thrown(TeamParser.InvalidInput)
    }

    @Unroll
    def "invalid input '#input'"() {
        when:
        parsePerson(input)
        then:
        thrown(TeamParser.InvalidInput)
        where:
        input << ["", "  ", ":", "a:", ":b", "a:b:c"]
    }
}
