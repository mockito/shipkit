package org.mockito.release.notes.format

import org.mockito.release.notes.contributors.ContributorsSet
import org.mockito.release.notes.contributors.DefaultContributorsSet
import org.mockito.release.notes.internal.DefaultImprovement
import org.mockito.release.notes.internal.DefaultReleaseNotesData
import org.mockito.release.notes.model.Commit
import org.mockito.release.notes.model.ContributionSet
import spock.lang.Specification

class NotableFormatterTest extends Specification {

    def "formats notes"() {
        def c = Stub(ContributionSet) {
            getAllCommits() >> [Stub(Commit), Stub(Commit)]
            getAuthorCount() >> 2
        }

        def i1 = [new DefaultImprovement(100, "Fixed issue", "http://issues/100", ["bugfix"], true),
                  new DefaultImprovement(103, "New feature", "http://issues/103", ["noteworthy"], true)]

        def i2 = [new DefaultImprovement(105, "Big change", "http://issues/105", [], true)]

        def contributors = new DefaultContributorsSet()

        def data = [new DefaultReleaseNotesData("1.1.0", new Date(1486700000000), c, i2, contributors, "v1.0.0", "v1.1.0"),
                    new DefaultReleaseNotesData("1.0.0", new Date(1486200000000), c, i1, contributors, "v0.0.9", "v1.0.0")]

        when:
        def text = new NotableFormatter("Mockito release notes:\n\n",
                "http://release-notes", "https://github.com/mockito/mockito/compare/{0}...{1}").formatReleaseNotes(data)

        then:
        text == """Mockito release notes:

### 1.1.0 - 2017-02-10

Authors: [2](http://release-notes), commits: [2](https://github.com/mockito/mockito/compare/v1.0.0...v1.1.0), improvements: [1](http://release-notes).

 * Big change [(#105)](http://issues/105)

### 1.0.0 - 2017-02-04

Authors: [2](http://release-notes), commits: [2](https://github.com/mockito/mockito/compare/v0.0.9...v1.0.0), improvements: [2](http://release-notes).

 * Fixed issue [(#100)](http://issues/100)
 * New feature [(#103)](http://issues/103)

"""
    }

    def "empty release notes"() {
        def data = [new DefaultReleaseNotesData("1.1.0", new Date(1486700000000), Stub(ContributionSet), [], Stub(ContributorsSet), "v1.0.0", "v1.1.0")]

        when:
        def text = new NotableFormatter(null, "http://detailed", "http://commits").formatReleaseNotes(data)

        then:
        text == """### 1.1.0 - 2017-02-10

No code changes. No commits found.

"""
    }

    def "no improvements"() {
        def c = Stub(ContributionSet) {
            getAllCommits() >> [Stub(Commit), Stub(Commit)]
            getAuthorCount() >> 2
        }

        def data = [new DefaultReleaseNotesData("1.1.0", new Date(1486700000000), c, [], Stub(ContributorsSet), "v1.0.0", "v1.1.0")]

        when:
        def text = new NotableFormatter(null, "http://detailed", "http://commits/{0}..{1}").formatReleaseNotes(data)

        then:
        text == """### 1.1.0 - 2017-02-10

Authors: [2](http://detailed), commits: [2](http://commits/v1.0.0..v1.1.0), improvements: [0](http://detailed).

No notable improvements. No pull requests were referenced from [commits](http://commits/v1.0.0..v1.1.0).

"""
    }
}
