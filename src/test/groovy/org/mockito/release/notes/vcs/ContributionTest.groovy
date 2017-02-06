package org.mockito.release.notes.vcs

import spock.lang.Specification

class ContributionTest extends Specification {

    def "accumulates commits"() {
        def c = new DefaultContribution(new GitCommit("a@b", "lad", "m1"))

        expect:
        c.authorName == "lad"
        c.authorEmail == "a@b"
        c.commits.size() == 1

        when: c.add(new GitCommit("a@b", "lad", "m2"))
        then: c.commits.size() == 2
    }

    def "can be sorted by number of commits"() {
        def c = new GitCommit("a", "a", "1")
        def c1 = new DefaultContribution(c)
        def c2 = new DefaultContribution(c).add(c)
        def c3 = new DefaultContribution(c).add(c).add(c)

        def set = new TreeSet([c1, c3, c2])

        expect:
        c1.commits.size() == 1
        c2.commits.size() == 2
        c3.commits.size() == 3

        set as List == [c3, c2, c1]
    }

    def "t2"() {
        def com1 = new GitCommit("a@x", "A", "1")
        def com2 = new GitCommit("b@x", "B", "2")
        def com3 = new GitCommit("b@x", "B", "3")

        def c1 = new DefaultContribution(com1)
        def c2 = new DefaultContribution(com2).add(com3)

        def set = new TreeSet()
        set.add(c1)
        set.add(c2)

        expect:
        for (Object c : set) {
            println c
        }
        set as List == [c2, c1]
    }

    def "sorted by commits and uppercase name"() {
        def d = new DefaultContribution(new GitCommit("d", "d", "")).add(new GitCommit("d", "d", ""))
        def c = new DefaultContribution(new GitCommit("c", "c", ""))
        def b = new DefaultContribution(new GitCommit("B", "B", ""))
        def a = new DefaultContribution(new GitCommit("a", "a", ""))

        expect:
        new TreeSet([d, c, b, a]) as List == [d, a, b, c]
    }
}
