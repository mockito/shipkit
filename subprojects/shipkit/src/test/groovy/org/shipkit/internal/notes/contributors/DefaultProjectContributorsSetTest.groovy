package org.shipkit.internal.notes.contributors

import spock.lang.Specification

class DefaultProjectContributorsSetTest extends Specification {

    def set = new DefaultProjectContributorsSet(["ignoredContributor"])

    def "does not replace existing contributor"() {
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

    def "does not replace existing contributors and ensures sort order"() {
        set.addAllContributors([
            new DefaultProjectContributor("a", "a", "a", 10),
            new DefaultProjectContributor("b", "b", "b", 10)
        ])
        set.addAllContributors([
            new DefaultProjectContributor("a", "a", "a", 1),
            new DefaultProjectContributor("c", "c", "c", 1)
        ])

        expect:
        set.allContributors.toString() == "[b/b[10], a/a[10], c/c[1]]"
    }

    def "does not drop contributors with the same amount of contributions"() {
        set.addContributor(new DefaultProjectContributor(
                "Szczepan Faber 1", "mockitoguy", "http://github.com/mockitoguy", 2000))
        set.addContributor(new DefaultProjectContributor(
                "Szczepan Faber 2", "mockitoguy", "http://github.com/mockitoguy", 2000))

        expect:
        set.allContributors.size() == 2
    }

    def "finds by name"() {
        set.addAllContributors([
                new DefaultProjectContributor("a", "a", "a", 10),
                new DefaultProjectContributor("b", "b", "b", 10)
        ])

        expect:
        set.findByName("c") == null
        set.findByName("b").name == "b"
        set.findByName("a").name == "a"
    }

    def "empty to config notation"() {
        expect:
        set.toConfigNotation() == []
    }

    def "two contributors to config notation"() {
        set.addAllContributors([
                new DefaultProjectContributor("aa", "a", "a", 10),
                new DefaultProjectContributor("bb", "b", "b", 5)
        ])

        expect:
        set.toConfigNotation() == ["a:aa", "b:bb"]
    }

    def "empty GitHub name to config notation"() {
        set.addAllContributors([
                new DefaultProjectContributor("", "login", "a", 10),
        ])

        expect:
        set.toConfigNotation() == ["login:login"]
    }

    def "ignores contributor"() {
        set.addAllContributors([
                new DefaultProjectContributor("name1", "notIgnoredContributor1", "a", 10),
                new DefaultProjectContributor("ignoredContributor", "ignoredContributor", "a", 10),
                new DefaultProjectContributor("name2", "notIgnoredContributor2", "a", 5)
        ])

        expect:
        set.findByName("ignoredContributor") == null
        set.size() == 2
        set.toConfigNotation() == ["notIgnoredContributor1:name1", "notIgnoredContributor2:name2"]
    }
}
