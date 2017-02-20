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

**2.0.0** - 1 commit by Szczepan Faber - *2017-01-04*
:cocktail: No pull requests referenced in commit messages."""
    }

    def "formats no improvements"() {
        expect:
        DetailedFormatter.formatImprovements([]) == ":cocktail: No pull requests referenced in commit messages."
    }

    def "formats few improvements"() {
        def i = [new DefaultImprovement(100, "Fixed issue", "http://issues/100", ["bugfix"], true),
                  new DefaultImprovement(103, "New feature", "http://issues/103", ["noteworthy"], true)]

        expect:
        DetailedFormatter.formatImprovements(i) == """:cocktail: Fixed issue [(#100)](http://issues/100)
:cocktail: New feature [(#103)](http://issues/103)"""
    }

    def "release headline with no commits"() {
        expect:
        DetailedFormatter.releaseHeadline(Stub(ContributionSet)) == "no code changes (no commits)"
    }

    def "release headline with 1 commit"() {
        def c = Stub(ContributionSet) {
            getAllCommits() >> [Stub(Commit)]
            getAuthorCount() >> 1
            getContributions() >> [Stub(Contribution) { getAuthorName() >> "Szczepan Faber"}]
        }

        expect:
        DetailedFormatter.releaseHeadline(c) == "1 commit by Szczepan Faber"
    }

    def "release headline with multiple authors"() {
        def c = Stub(ContributionSet) {
            getAllCommits() >> [Stub(Commit), Stub(Commit), Stub(Commit), Stub(Commit)]
            getAuthorCount() >> 2
            getContributions() >> [
                    Stub(Contribution) { getAuthorName() >> "Szczepan Faber"},
                    Stub(Contribution) { getAuthorName() >> "Brice Dutheil"}]
        }

        expect:
        DetailedFormatter.releaseHeadline(c) == "4 commits by Szczepan Faber, Brice Dutheil"
    }

    def "release headline with many authors"() {
        def c = Stub(ContributionSet) {
            getAllCommits() >> [Stub(Commit)] * 100 //100 commits
            getAuthorCount() >> 10
            getContributions() >> [Stub(Contribution)] * 10 //10 authors
        }

        expect:
        DetailedFormatter.releaseHeadline(c) == "100 commits by 10 authors"
    }
}
