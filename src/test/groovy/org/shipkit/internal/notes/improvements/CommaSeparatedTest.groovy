package org.shipkit.internal.notes.improvements

import spock.lang.Specification

class CommaSeparatedTest extends Specification {

    def "comma separated list"() {
        expect:
        org.shipkit.internal.notes.improvements.CommaSeparated.commaSeparated([]) == ''
        org.shipkit.internal.notes.improvements.CommaSeparated.commaSeparated(['a']) == 'a'
        org.shipkit.internal.notes.improvements.CommaSeparated.commaSeparated(['a', 'b']) == 'a,b'
        org.shipkit.internal.notes.improvements.CommaSeparated.commaSeparated(['b', 'a']) == 'b,a'
        org.shipkit.internal.notes.improvements.CommaSeparated.commaSeparated(['a b', 'c d']) == 'a b,c d'
    }
}
