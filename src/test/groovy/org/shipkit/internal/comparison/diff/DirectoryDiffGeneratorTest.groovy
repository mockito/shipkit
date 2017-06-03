package org.shipkit.internal.comparison.diff

import spock.lang.Specification

class DirectoryDiffGeneratorTest extends Specification {

    def "handles empty lists correctly"() {
        expect:
        new DirectoryDiffGenerator().generateDiffOutput([], [], []) == ""
    }

    def "handles null lists correctly"() {
        expect:
        new DirectoryDiffGenerator().generateDiffOutput(null, null, null) == ""
    }

    def "generates diffOutput correctly"() {
        when:
        def result = new DirectoryDiffGenerator().generateDiffOutput(["6.txt"], ["1.txt", "5.txt"], ["2.txt"])

        then:
        result ==
                """    Added files:
    ++ 6.txt

    Removed files:
    -- 1.txt
    -- 5.txt

    Modified files:
    +- 2.txt

"""
    }
}
