package org.mockito.release.notes.format

import org.mockito.release.notes.improvements.Improvement
import org.mockito.release.notes.util.Predicate
import org.mockito.release.notes.vcs.ContributionSet
import org.mockito.release.notes.vcs.DefaultContributionSet
import org.mockito.release.notes.vcs.GitCommit
import spock.lang.Specification

class DefaultFormatterTest extends Specification {

    def "empty improvements"() {
        expect:
        DefaultFormatter.format([:], []) == "* No notable improvements. See the commits for detailed changes."
    }

    def "set of improvements in order"() {
        def labels = [bug: "Bugfixes", enhancement: "Enhancements"]
        def is = [new Improvement(100, "Fix bug x", "http://issues/100", ["bug"]),
            new Improvement(122, "Javadoc update", "http://url/122", []),
            new Improvement(125, "Some enh", "http://issues/125", ["java-8", "enhancement", "bug"]),
            new Improvement(126, "Some other enh", "http://issues/126", ["enhancement"]),
            new Improvement(130, "Refactoring", "http://issues/130", ["java-8", "refactoring"])]

        expect:
        DefaultFormatter.format(labels, is) == """* Improvements: 5
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
        DefaultFormatter.format([bug: "Bugfixes"], [new Improvement(10, "Issue 10", "10", [])]) == """* Improvements: 1
  * Issue 10 [(#10)](10)"""
    }

    def "no duplicated improvements"() {
        given:
        def labels = [bug: "Bugfixes", refactoring: "Refactorings"]
        def is = [new Improvement(10, "Issue 10", "10", ["bug", "refactoring"]),
            new Improvement(11, "Issue 11", "11", ["refactoring", "bug"])]

        expect: "no duplication even though labels are overused"
        DefaultFormatter.format(labels, is) == """* Improvements: 2
  * Bugfixes: 2
    * Issue 10 [(#10)](10)
    * Issue 11 [(#11)](11)"""
    }

    def "the order of labels is determined"() {
        given:
        //input label captions determine the order of labels:
        def labels = [p0: "Priority 0", p1: "Priority 1"]
        def imp1 = new Improvement(10, "Issue 10", "10", ["p0"])
        def imp2 = new Improvement(11, "Issue 11", "11", ["p1"])

        when:
        def improvements = DefaultFormatter.format(labels, [imp1, imp2])
        def reordered = DefaultFormatter.format(labels, [imp2, imp1])

        then: "The order of labels is determined"
        improvements == reordered
    }

    def "formats notes"() {
        def date = new Date(1483570800000)
        def is = [new Improvement(100, "Fix bug x", "http://issues/100", ["bug"])]
        def contributions = new DefaultContributionSet({false} as Predicate).add(new GitCommit("a", "a", "m"))
        when: def notes = DefaultFormatter.formatNotes("2.0.1", date, contributions, [:], is)
        then: notes == """### 2.0.1 (2017-01-04 23:00 UTC)

* Authors: 1
* Commits: 1
  * 1: a
* Improvements: 1
  * Fix bug x [(#100)](http://issues/100)

"""
    }
}
