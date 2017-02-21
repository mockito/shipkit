package org.mockito.release.notes.format

import org.mockito.release.notes.contributors.DefaultContributor
import org.mockito.release.notes.contributors.DefaultContributorsMap
import org.mockito.release.notes.internal.DefaultReleaseNotesData
import org.mockito.release.notes.internal.DefaultImprovement
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
        def is = [new DefaultImprovement(100, "Fix bug x", "http://issues/100", ["bug"], true),
                  new DefaultImprovement(122, "Javadoc update", "http://url/122", [], true),
                  new DefaultImprovement(125, "Some enh", "http://issues/125", ["java-8", "enhancement", "bug"], true),
                  new DefaultImprovement(126, "Some other enh", "http://issues/126", ["enhancement"], true),
                  new DefaultImprovement(130, "Refactoring", "http://issues/130", ["java-8", "refactoring"], true)]

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
        f.format([bug: "Bugfixes"], [new DefaultImprovement(10, "Issue 10", "10", [], true)]) == """* Improvements: 1
  * Issue 10 [(#10)](10)"""
    }

    def "no duplicated improvements"() {
        given:
        def labels = [bug: "Bugfixes", refactoring: "Refactorings"]
        def is = [new DefaultImprovement(10, "Issue 10", "10", ["bug", "refactoring"], true),
                  new DefaultImprovement(11, "Issue 11", "11", ["refactoring", "bug"], true)]

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
        def imp1 = new DefaultImprovement(10, "Issue 10", "10", ["p0"], true)
        def imp2 = new DefaultImprovement(11, "Issue 11", "11", ["p1"], true)

        when:
        def improvements = f.format(labels, [imp1, imp2])
        def reordered = f.format(labels, [imp2, imp1])

        then: "The order of labels is determined"
        improvements == reordered
    }

    def "many contributions, no profile URLs"() {
        ContributionSet contributions = new DefaultContributionSet({false} as Predicate)

        contributions.add(new GitCommit("1", "a@x", "Monalisa Octocat", "1"))
        contributions.add(new GitCommit("2", "b@x", "CD Drone", "2"))
        contributions.add(new GitCommit("3", "b@x", "CD Drone", "3"))

        def contributors = new DefaultContributorsMap()

        expect:
        f.format(contributions, contributors) == """* Authors: 2
* Commits: 3
  * 2: CD Drone
  * 1: Monalisa Octocat"""
    }

    def "many contributions and profile URLs"() {
        ContributionSet contributions = new DefaultContributionSet({false} as Predicate)

        contributions.add(new GitCommit("1", "a@x", "Monalisa Octocat", "1"))
        contributions.add(new GitCommit("2", "b@x", "CD Drone", "2"))
        contributions.add(new GitCommit("3", "b@x", "CD Drone", "3"))

        def contributors = new DefaultContributorsMap()
        contributors.put("Monalisa Octocat", new DefaultContributor("Monalisa Octocat", "octocat", "http://gh.com/octocat"))
        contributors.put("CD Drone", new DefaultContributor("CD Drone", "cddrone", "http://gh.com/cddrone"))

        expect:
        f.format(contributions, contributors) == """* Authors: 2
* Commits: 3
  * 2: [CD Drone](http://gh.com/cddrone)
  * 1: [Monalisa Octocat](http://gh.com/octocat)"""
    }

    def "empty contributions"() {
        ContributionSet contributions = new DefaultContributionSet({false} as Predicate)
        def contributors = new DefaultContributorsMap()

        expect:
        f.format(contributions, contributors) == "* Authors: 0\n* Commits: 0"
    }

    def "contributions by same author with different email, no profile URLs"() {
        ContributionSet contributions = new DefaultContributionSet({false} as Predicate)

        contributions.add(new GitCommit("", "john@x", "john", ""))
        contributions.add(new GitCommit("", "john@x", "john", ""))
        contributions.add(new GitCommit("", "john@y", "john", "")) //same person, different email
        contributions.add(new GitCommit("", "x@y", "x", "")) //different person

        def contributors = new DefaultContributorsMap()

        expect:
        f.format(contributions, contributors) == """* Authors: 2
* Commits: 4
  * 3: john
  * 1: x"""
    }

    def "contributions by same author with different email and profile URLs"() {
        ContributionSet contributions = new DefaultContributionSet({false} as Predicate)

        contributions.add(new GitCommit("", "john@x", "john", ""))
        contributions.add(new GitCommit("", "john@x", "john", ""))
        contributions.add(new GitCommit("", "john@y", "john", "")) //same person, different email
        contributions.add(new GitCommit("", "x@y", "x", "")) //different person

        def contributors = new DefaultContributorsMap()
        contributors.put("john", new DefaultContributor("john", "johnx", "gh/johnx"))
        contributors.put("x", new DefaultContributor("x", "x", "gh/x"))

        expect:
        f.format(contributions, contributors) == """* Authors: 2
* Commits: 4
  * 3: [john](gh/johnx)
  * 1: [x](gh/x)"""
    }

    def "contributions sorted by name if number of commits the same, no profile URLs"() {
        ContributionSet contributions = new DefaultContributionSet({false} as Predicate)

        contributions.add(new GitCommit("", "d@d", "d", ""))
        contributions.add(new GitCommit("", "d@d", "d", ""))
        contributions.add(new GitCommit("", "c@c", "c", ""))
        contributions.add(new GitCommit("", "B@B", "B", ""))
        contributions.add(new GitCommit("", "a@a", "a", ""))

        def contributors = new DefaultContributorsMap()

        expect:
        f.format(contributions, contributors) == """* Authors: 4
* Commits: 5
  * 2: d
  * 1: a
  * 1: B
  * 1: c"""
    }

    def "contributions sorted by name if number of commits the same and profile URLs"() {
        ContributionSet contributions = new DefaultContributionSet({false} as Predicate)

        contributions.add(new GitCommit("", "d@d", "d", ""))
        contributions.add(new GitCommit("", "d@d", "d", ""))
        contributions.add(new GitCommit("", "c@c", "c", ""))
        contributions.add(new GitCommit("", "B@B", "B", ""))
        contributions.add(new GitCommit("", "a@a", "a", ""))

        def contributors = new DefaultContributorsMap()
        contributors.put("d", new DefaultContributor("d", "dd", "gh/dd"))
        contributors.put("c", new DefaultContributor("c", "cc", "gh/cc"))
        contributors.put("B", new DefaultContributor("B", "BB", "gh/BB"))
        contributors.put("a", new DefaultContributor("a", "aa", "gh/aa"))

        expect:
        f.format(contributions, contributors) == """* Authors: 4
* Commits: 5
  * 2: [d](gh/dd)
  * 1: [a](gh/aa)
  * 1: [B](gh/BB)
  * 1: [c](gh/cc)"""
    }

    def "formats notes"() {
        def date = new Date(1483570800000)
        def is = [new DefaultImprovement(100, "Fix bug x", "http://issues/100", ["bug"], true)]
        def contributions = new DefaultContributionSet({false} as Predicate).add(new GitCommit("", "a", "a", "m"))
        def contributors = new DefaultContributorsMap()

        when: def notes = f.formatVersion(new DefaultReleaseNotesData("2.0.1", date, contributions, is, contributors, "v2.0.0", "v2.0.1"))


* Authors: 1
* Commits: 1
  * 1: a
* Improvements: 1
  * Fix bug x [(#100)](http://issues/100)

"""
    }
}
