package org.mockito.release.notes.contributors

import spock.lang.Specification

class DefaultProjectContributorsSetTest extends Specification {

    def "does not replace existing contributor"() {
        def set = new DefaultProjectContributorsSet()
        set.addContributor(new DefaultProjectContributor("a", "a", "a", 2000))
        //this is important use case because of how we get contributors from GitHub.
        // We issue 2 queries to GitHub, first query gets us most contributors, second gets us most recent contributors
        // Same contributor with just one contribution:
        set.addContributor(new DefaultProjectContributor("a", "a", "a", 1))

        expect:
        def c = set.allContributors as List
        c.size() == 1
        c[0].numberOfContributions == 2000
    }

    def "does not drop contributors with the same amount of contributions"() {
        def set = new DefaultProjectContributorsSet()
        set.addContributor(new DefaultProjectContributor(
                "Szczepan Faber 1", "szczepiq", "http://github.com/szczepiq", 2000))
        set.addContributor(new DefaultProjectContributor(
                "Szczepan Faber 2", "szczepiq", "http://github.com/szczepiq", 2000))

        expect:
        set.allContributors.size() == 2
    }
}
