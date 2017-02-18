package org.mockito.release.notes.format

import org.mockito.release.notes.internal.DefaultImprovement
import org.mockito.release.notes.internal.DefaultReleaseNotesData
import org.mockito.release.notes.model.Commit
import org.mockito.release.notes.model.ContributionSet
import spock.lang.Specification

class ConciseFormatterTest extends Specification {

    def "formats notes"() {
        def c = Stub(ContributionSet) {
            getAllCommits() >> [Stub(Commit), Stub(Commit)]
            getAuthorCount() >> 2
        }

        def i1 = [new DefaultImprovement(100, "Fixed issue", "http://issues/100", ["bugfix"], true),
                  new DefaultImprovement(103, "New feature", "http://issues/103", ["noteworthy"], true)]

        def i2 = [new DefaultImprovement(105, "Big change", "http://issues/105", [], true)]

        def data = [new DefaultReleaseNotesData("1.1.0", new Date(1486700000000), c, i2, "v1.0.0", "v1.1.0"),
                    new DefaultReleaseNotesData("1.0.0", new Date(1486200000000), c, i1, "v0.0.9", "v1.0.0")]

        when:
        def text = new ConciseFormatter("Mockito release notes:\n\n", "http://release-notes").formatReleaseNotes(data)

        then:
        text == """Mockito release notes:

### 1.1.0 - 2017-02-10 04:13

Authors: [2](http://release-notes), commits: [2](https://github.com/mockito/mockito/compare/v1.0.0...v1.1.0), improvements: [1](http://release-notes).

 * Big change [(#105)](http://issues/105)

### 1.0.0 - 2017-02-04 09:20

Authors: [2](http://release-notes), commits: [2](https://github.com/mockito/mockito/compare/v0.0.9...v1.0.0), improvements: [2](http://release-notes).

 * Fixed issue [(#100)](http://issues/100)
 * New feature [(#103)](http://issues/103)

"""
    }
}
