package org.shipkit.internal.notes.format

import org.shipkit.internal.notes.contributors.DefaultContributor
import org.shipkit.internal.notes.internal.DefaultImprovement
import org.shipkit.internal.notes.internal.DefaultReleaseNotesData
import org.shipkit.internal.notes.model.Commit
import org.shipkit.internal.notes.model.Contribution
import org.shipkit.internal.notes.model.ContributionSet
import spock.lang.Specification

class DetailedFormatterTest extends Specification {

    def f = new DetailedFormatter("Info about shipkit\n\n", "Release notes:\n\n", ["noteworthy": "Noteworthy", "bug": "Bugfixes"],
            "http://commits/{0}...{1}", "Bintray/", [:], false)

    def "no releases"() {
        expect:
        f.formatReleaseNotes([]) == """Info about shipkit

Release notes:

No release information."""
    }

    def "empty releases"() {
        def d1 = new DefaultReleaseNotesData("2.0.0", new Date(1483500000000), Stub(ContributionSet), [], "v1.9.0", "v2.0.0")
        def d2 = new DefaultReleaseNotesData("1.9.0", new Date(1483100000000), Stub(ContributionSet), [], "v1.8.0", "v1.9.0")

        expect:
        f.formatReleaseNotes([d1, d2]) == """Info about shipkit

Release notes:

#### 2.0.0
 - 2017-01-04 - no code changes (no commits) - published to [![Bintray](https://img.shields.io/badge/Bintray-2.0.0-green.svg)](Bintray/2.0.0)

#### 1.9.0
 - 2016-12-30 - no code changes (no commits) - published to [![Bintray](https://img.shields.io/badge/Bintray-1.9.0-green.svg)](Bintray/1.9.0)"""
    }

    def "no improvements"() {
        def c = Stub(ContributionSet) {
            getAllCommits() >> [Stub(Commit)]
            getAuthorCount() >> 1
            getContributions() >> [Stub(Contribution) {
                getAuthorName() >> "Szczepan Faber"
            } ]
        }

        def d = new DefaultReleaseNotesData("2.0.0", new Date(1483500000000), c, [], "v1.9.0", "v2.0.0")

        expect:
        f.formatReleaseNotes([d]) == """Info about shipkit

Release notes:

#### 2.0.0
 - 2017-01-04 - [1 commit](http://commits/v1.9.0...v2.0.0) by Szczepan Faber - published to [![Bintray](https://img.shields.io/badge/Bintray-2.0.0-green.svg)](Bintray/2.0.0)
 - No pull requests referenced in commit messages."""
    }

    def "formats header when emphasized version"() {
        expect:
        DetailedFormatter.header("v0.1.0", false) == "#### v0.1.0"
    }

    def "formats header when regular version"() {
        expect:
        DetailedFormatter.header("v0.1.0", true) == "# v0.1.0"
    }

    def "formats no improvements"() {
        expect:
        DetailedFormatter.formatImprovements([], [:]) == " - No pull requests referenced in commit messages."
    }

    def "formats few improvements"() {
        def i = [new DefaultImprovement(100, "Fixed issue", "http://issues/100", ["bugfix"], true),
                  new DefaultImprovement(103, "New feature", "http://issues/103", ["noteworthy"], true)]

        def labelMapping = [bugfix: "Bugfixes"]

        expect:
        //issues that have label that matches the mapping have an extra label prefix
        DetailedFormatter.formatImprovements(i, labelMapping) == """ - [Bugfixes] Fixed issue [(#100)](http://issues/100)
 - New feature [(#103)](http://issues/103)"""
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
        DetailedFormatter.formatImprovements(i, labelMapping) == """ - [Noteworthy] Fixed major issue [(#103)](http://issues/103)
 - [Bugfixes] Fixed problem [(#100)](http://issues/100)
 - [Bugfixes] Fixed bugs in javadoc [(#106)](http://issues/106)
 - Refactoring [(#105)](http://issues/105)
 - Big refactoring [(#107)](http://issues/107)
 - Small tweak [(#108)](http://issues/108)"""
    }

    def "release headline with no commits"() {
        expect:
        DetailedFormatter.authorsSummary(Stub(ContributionSet), [:], "link") == "no code changes (no commits)"
    }

    def "authors summary with 1 commit"() {
        def c = Stub(ContributionSet) {
            getAllCommits() >> [Stub(Commit)]
            getAuthorCount() >> 1
            getContributions() >> [c("Szczepan Faber", 1)]
        }

        expect:
        DetailedFormatter.authorsSummary(c,
                ["Szczepan Faber": new DefaultContributor("Szczepan Faber", "mockitoguy", "http://github.com/mockitoguy") ]
                , "link") == "[1 commit](link) by [Szczepan Faber](http://github.com/mockitoguy)"
    }

    def "authors summary with multiple authors"() {
        def c = Stub(ContributionSet) {
            getAllCommits() >> [Stub(Commit), Stub(Commit), Stub(Commit), Stub(Commit)]
            getAuthorCount() >> 2
            getContributions() >> [ c("Szczepan Faber", 2), c("Brice Dutheil", 2)]
        }

        expect:
        DetailedFormatter.authorsSummary(c, [:], "link") == "[4 commits](link) by Szczepan Faber (2), Brice Dutheil (2)"
    }

    def "authors summary with many authors"() {
        def c = Stub(ContributionSet) {
            getAllCommits() >> [Stub(Commit)] * 100 //100 commits
            getAuthorCount() >> 10
            getContributions() >> [Stub(Contribution)] * 10 //10 authors
        }

        expect:
        DetailedFormatter.authorsSummary(c, [:], "link") == "[100 commits](link) by 10 authors"
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

        def summary = DetailedFormatter.releaseSummary(new Date(1483500000000), "1.2.3", c, [:], "link",
                "https://bintray.com/shipkit/")

        expect:
        summary == """ - 2017-01-04 - [100 commits](link) by 4 authors - published to [![Bintray](https://img.shields.io/badge/Bintray-1.2.3-green.svg)](https://bintray.com/shipkit/1.2.3)
 - Commits: Szczepan Faber (40), Brice Dutheil (30), Rafael Winterhalter (20), Tim van der Lippe (10)
"""
    }

    def "contribution with unmapped contributor"() {
        def c = Stub(Contribution) {
            getAuthorName() >> "John"
        }

        expect:
        DetailedFormatter.authorLink(c, null) == "John"
    }

    private Contribution c(String name, int commits) {
        return Stub(Contribution) {
            getAuthorName() >> name
            getCommits() >> [Stub(Commit)] * commits
        }
    }
}
