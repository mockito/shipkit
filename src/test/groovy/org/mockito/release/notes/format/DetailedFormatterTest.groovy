package org.mockito.release.notes.format

import org.mockito.release.notes.internal.DefaultImprovement
import org.mockito.release.notes.internal.DefaultReleaseNotesData
import org.mockito.release.notes.model.Commit
import org.mockito.release.notes.model.Contribution
import org.mockito.release.notes.model.ContributionSet
import spock.lang.Specification

class DetailedFormatterTest extends Specification {

    def f = new DetailedFormatter("Release notes:\n\n", ["noteworthy": "Noteworthy", "bug": "Bugfixes"], "http://commits/{0}...{1}")

    def "no releases"() {
        expect:
        f.formatReleaseNotes([]) == """Release notes:

No release information."""
    }

    def "empty releases"() {
        def d1 = new DefaultReleaseNotesData("2.0.0", new Date(1483500000000), Stub(ContributionSet), [], "v1.9.0", "v2.0.0")
        def d2 = new DefaultReleaseNotesData("1.9.0", new Date(1483100000000), Stub(ContributionSet), [], "v1.8.0", "v1.9.0")

        expect:
        f.formatReleaseNotes([d1, d2]) == """Release notes:

**2.0.0** - no code changes (no commits) - *2017-01-04*

**1.9.0** - no code changes (no commits) - *2016-12-30*"""
    }

    def "no improvements"() {
        def c = Stub(ContributionSet) {
            getAllCommits() >> [Stub(Commit)]
            getAuthorCount() >> 1
            getContributions() >> [Stub(Contribution) { getAuthorName() >> "Szczepan Faber"}]
        }

        def d = new DefaultReleaseNotesData("2.0.0", new Date(1483500000000), c, [], "v1.9.0", "v2.0.0")

        expect:
        f.formatReleaseNotes([d]) == """Release notes:

**2.0.0** - [1 commit](http://commits/v1.9.0...v2.0.0) by Szczepan Faber - *2017-01-04*
:cocktail: No pull requests referenced in commit messages."""
    }

    def "formats no improvements"() {
        expect:
        DetailedFormatter.formatImprovements([], [:]) == ":cocktail: No pull requests referenced in commit messages."
    }

    def "formats few improvements"() {
        def i = [new DefaultImprovement(100, "Fixed issue", "http://issues/100", ["bugfix"], true),
                  new DefaultImprovement(103, "New feature", "http://issues/103", ["noteworthy"], true)]

        def labelMapping = [bugfix: "Bugfixes"]

        expect:
        //issues that have label that matches the mapping have an extra label prefix
        DetailedFormatter.formatImprovements(i, labelMapping) == """:cocktail: [Bugfixes] Fixed issue [(#100)](http://issues/100)
:cocktail: New feature [(#103)](http://issues/103)"""
    }

    def "formats and sorts many improvements"() {
        def i = [new DefaultImprovement(100, "Fixed problem",         "http://issues/100", ["bugfix"], true),
                 new DefaultImprovement(103, "Fixed major issue",     "http://issues/103", ["noteworthy", "bugfix"], true),
                 new DefaultImprovement(105, "Refactoring",           "http://issues/105", [], true),
                 new DefaultImprovement(106, "Fixed bugs in javadoc", "http://issues/106", ["docs", "bugfix"], true),
                 new DefaultImprovement(107, "Big refactoring", "http://issues/107", ["refactoring"], true),
                 new DefaultImprovement(108, "Small tweak", "http://issues/108", [], true)]

        def labelMapping = [noteworthy: "Noteworthy", bugfix: "Bugfixes"]

        expect:
        //improvements are sorted based on the label mapping
        //if an issue has labels that match multiple mapping, first mapping in label mapping wins
        DetailedFormatter.formatImprovements(i, labelMapping) == """:cocktail: [Noteworthy] Fixed major issue [(#103)](http://issues/103)
:cocktail: [Bugfixes] Fixed problem [(#100)](http://issues/100)
:cocktail: [Bugfixes] Fixed bugs in javadoc [(#106)](http://issues/106)
:cocktail: Refactoring [(#105)](http://issues/105)
:cocktail: Big refactoring [(#107)](http://issues/107)
:cocktail: Small tweak [(#108)](http://issues/108)"""
    }

    def "release headline with no commits"() {
        expect:
        DetailedFormatter.authorsSummary(Stub(ContributionSet), "link") == "no code changes (no commits)"
    }

    def "authors summary with 1 commit"() {
        def c = Stub(ContributionSet) {
            getAllCommits() >> [Stub(Commit)]
            getAuthorCount() >> 1
            getContributions() >> [c("Szczepan Faber", 1)]
        }

        expect:
        DetailedFormatter.authorsSummary(c, "link") == "[1 commit](link) by Szczepan Faber"
    }

    def "authors summary with multiple authors"() {
        def c = Stub(ContributionSet) {
            getAllCommits() >> [Stub(Commit), Stub(Commit), Stub(Commit), Stub(Commit)]
            getAuthorCount() >> 2
            getContributions() >> [ c("Szczepan Faber", 2), c("Brice Dutheil", 2)]
        }

        expect:
        DetailedFormatter.authorsSummary(c, "link") == "[4 commits](link) by Szczepan Faber (2), Brice Dutheil (2)"
    }

    def "authors summary with many authors"() {
        def c = Stub(ContributionSet) {
            getAllCommits() >> [Stub(Commit)] * 100 //100 commits
            getAuthorCount() >> 10
            getContributions() >> [Stub(Contribution)] * 10 //10 authors
        }

        expect:
        DetailedFormatter.authorsSummary(c, "link") == "[100 commits](link) by 10 authors"
    }

    def "release notes with many authors"() {
        def c = Stub(ContributionSet) {
            getAllCommits() >> [Stub(Commit)] * 100 //100 commits
            getAuthorCount() >> 4
            getContributions() >> [ c("Szczepan Faber", 40),
                                    c("Brice Dutheil", 30),
                                    c("Rafael Winterhalter", 20),
                                    c("Tim van der Lippe", 10)]
        }

        expect:
        DetailedFormatter.releaseSummary(new Date(1483500000000), c, "link") == """[100 commits](link) by 4 authors - *2017-01-04*
:cocktail: Commits: Szczepan Faber (40), Brice Dutheil (30), Rafael Winterhalter (20), Tim van der Lippe (10)"""
    }

    private Contribution c(String name, int commits) {
        return Stub(Contribution) {
            getAuthorName() >> name
            getCommits() >> [Stub(Commit)] * commits
        }
    }
}
