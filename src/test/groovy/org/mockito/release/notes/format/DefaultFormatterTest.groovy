package org.mockito.release.notes.format

import org.mockito.release.notes.internal.DefaultReleaseNotesData
import org.mockito.release.notes.improvements.DefaultImprovement
import org.mockito.release.notes.util.Predicate
import org.mockito.release.notes.model.ContributionSet
import org.mockito.release.notes.vcs.DefaultContributionSet
import org.mockito.release.notes.vcs.GitCommit
import spock.lang.Specification

class DefaultFormatterTest extends Specification {

    DefaultFormatter f = new DefaultFormatter([:])

    def "empty improvements"() {
        expect:
        f.format([:], []) == "* No notable improvements. See the commits for detailed changes."
    }

    def "set of improvements in order"() {
        def labels = [bug: "Bugfixes", enhancement: "Enhancements"]
        def is = [new DefaultImprovement(100, "Fix bug x", "http://issues/100", ["bug"]),
                  new DefaultImprovement(122, "Javadoc update", "http://url/122", []),
                  new DefaultImprovement(125, "Some enh", "http://issues/125", ["java-8", "enhancement", "bug"]),
                  new DefaultImprovement(126, "Some other enh", "http://issues/126", ["enhancement"]),
                  new DefaultImprovement(130, "Refactoring", "http://issues/130", ["java-8", "refactoring"])]

        expect:
        f.format(labels, is) == """* Improvements: 5
  * Bugfixes: 2
    * Fix bug x [(#100)](http://issues/100)
    * Some enh [(#125)](http://issues/125)
  * Enhancements: 1
    * Some other enh [(#126)](http://issues/126)
  * Remaining changes: 2
    * Javadoc update [(#122)](http://url/122)
    * Refactoring [(#130)](http://issues/130)"""
    }

    def "no matching labels"() {
        expect: "the formatting is simplified"
        f.format([bug: "Bugfixes"], [new DefaultImprovement(10, "Issue 10", "10", [])]) == """* Improvements: 1
  * Issue 10 [(#10)](10)"""
    }

    def "no duplicated improvements"() {
        given:
        def labels = [bug: "Bugfixes", refactoring: "Refactorings"]
        def is = [new DefaultImprovement(10, "Issue 10", "10", ["bug", "refactoring"]),
                  new DefaultImprovement(11, "Issue 11", "11", ["refactoring", "bug"])]

        expect: "no duplication even though labels are overused"
        f.format(labels, is) == """* Improvements: 2
  * Bugfixes: 2
    * Issue 10 [(#10)](10)
    * Issue 11 [(#11)](11)"""
    }

    def "the order of labels is determined"() {
        given:
        //input label captions determine the order of labels:
        def labels = [p0: "Priority 0", p1: "Priority 1"]
        def imp1 = new DefaultImprovement(10, "Issue 10", "10", ["p0"])
        def imp2 = new DefaultImprovement(11, "Issue 11", "11", ["p1"])

        when:
        def improvements = f.format(labels, [imp1, imp2])
        def reordered = f.format(labels, [imp2, imp1])

        then: "The order of labels is determined"
        improvements == reordered
    }

    def "many contributions"() {
        ContributionSet contributions = new DefaultContributionSet({false} as Predicate)

        contributions.add(new GitCommit("a@x", "A", "1"))
        contributions.add(new GitCommit("b@x", "B", "2"))
        contributions.add(new GitCommit("b@x", "B", "3"))

        expect:
        f.format(contributions) == """* Authors: 2
* Commits: 3
  * 2: B
  * 1: A"""
    }

    def "empty contributions"() {
        ContributionSet contributions = new DefaultContributionSet({false} as Predicate)
        expect:
        f.format(contributions) == "* Authors: 0\n* Commits: 0"
    }

    def "contributions by same author with different email"() {
        ContributionSet contributions = new DefaultContributionSet({false} as Predicate)

        contributions.add(new GitCommit("john@x", "john", ""))
        contributions.add(new GitCommit("john@x", "john", ""))
        contributions.add(new GitCommit("john@y", "john", "")) //same person, different email
        contributions.add(new GitCommit("x@y", "x", "")) //different person

        expect:
        f.format(contributions) == """* Authors: 2
* Commits: 4
  * 3: john
  * 1: x"""
    }

    def "contributions sorted by name if number of commits the same"() {
        ContributionSet contributions = new DefaultContributionSet({false} as Predicate)

        contributions.add(new GitCommit("d@d", "d", ""))
        contributions.add(new GitCommit("d@d", "d", ""))
        contributions.add(new GitCommit("c@c", "c", ""))
        contributions.add(new GitCommit("B@B", "B", ""))
        contributions.add(new GitCommit("a@a", "a", ""))

        expect:
        f.format(contributions) == """* Authors: 4
* Commits: 5
  * 2: d
  * 1: a
  * 1: B
  * 1: c"""
    }

    def "formats notes"() {
        def date = new Date(1483570800000)
        def is = [new DefaultImprovement(100, "Fix bug x", "http://issues/100", ["bug"])]
        def contributions = new DefaultContributionSet({false} as Predicate).add(new GitCommit("a", "a", "m"))
        when: def notes = f.formatVersion(new DefaultReleaseNotesData("2.0.1", date, contributions, is))
        then: notes == """### 2.0.1 (2017-01-04 23:00 UTC)

* Authors: 1
* Commits: 1
  * 1: a
* Improvements: 1
  * Fix bug x [(#100)](http://issues/100)

"""
    }
}
