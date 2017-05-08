package org.mockito.release.notes.contributors

import spock.lang.Specification

class DefaultProjectContributorTest extends Specification {

    def "equals"() {
        def contributor = new DefaultProjectContributor(
                "Szczepan Faber", "szczepiq", "http://github.com/szczepiq", 1)
        def same = new DefaultProjectContributor(
                "Szczepan Faber", "szczepiq", "http://github.com/szczepiq", 1)
        def differentName = new DefaultProjectContributor(
                "xxx", "szczepiq", "http://github.com/szczepiq", 1)
        def differentLogin = new DefaultProjectContributor(
                "Szczepan Faber", "xxx", "http://github.com/szczepiq", 1)
        def differentUrl = new DefaultProjectContributor(
                "Szczepan Faber", "szczepiq", "xxx", 1)
        def differentContributions = new DefaultProjectContributor(
                "Szczepan Faber", "szczepiq", "http://github.com/szczepiq", 10)

        expect:
        contributor == same
        same == same

        contributor != differentContributions
        contributor != differentName
        contributor != differentLogin
        contributor != differentUrl
    }

    def "comparable"() {
        def a1 = new DefaultProjectContributor("a", "a", "a", 1)
        def a1Copy = new DefaultProjectContributor("a", "a", "a", 1)
        def a2 = new DefaultProjectContributor("a", "a", "a", 2)
        def b10 = new DefaultProjectContributor("b", "b", "b", 10)
        def c1 = new DefaultProjectContributor("c", "c", "c", 1)

        expect:
        a1 < a2
        a2 < b10
        c1 < a2

        a2 > a1
        b10 > a2
        a2 > c1

        a1Copy.compareTo(a1) == 0
    }
}
