package org.mockito.release.notes.improvements

import spock.lang.Specification

class CommaSeparatedTest extends Specification {

    def "comma separated list"() {
        expect:
        CommaSeparated.commaSeparated([]) == ''
        CommaSeparated.commaSeparated(['a']) == 'a'
        CommaSeparated.commaSeparated(['a', 'b']) == 'a,b'
        CommaSeparated.commaSeparated(['b', 'a']) == 'b,a'
        CommaSeparated.commaSeparated(['a b', 'c d']) == 'a b,c d'
    }
}
