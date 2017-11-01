package org.shipkit.internal.comparison.diff

import spock.lang.Specification

class FileDiffGeneratorTest extends Specification {

    def "handles empty files"() {
        when:
        def result = new FileDiffGenerator().generateDiff("", "")

        then:
        result == ""
    }

    def "handles different files"() {
        when:
        def result = new FileDiffGenerator().generateDiff(
"""
aa
bb
cc
dd
ee
""",
"""
ee
bb
cc
ff
""")

        then:
        result ==
"""    @@ -2,1 +2,1 @@
    -aa
    +ee
    @@ -5,2 +5,1 @@
    -dd
    -ee
    +ff"""
    }

    def "handles same files"() {
        when:
        def result = new FileDiffGenerator().generateDiff("aa\nbb", "aa\nbb")

        then:
        result == ""
    }
}
