package org.mockito.release.notes.format

import org.mockito.release.notes.contributors.DefaultContributorsSet
import org.mockito.release.notes.internal.DefaultReleaseNotesData
import org.mockito.release.notes.internal.DefaultImprovement
import org.mockito.release.notes.util.Predicate
import org.mockito.release.notes.vcs.DefaultContributionSet
import org.mockito.release.notes.vcs.GitCommit
import spock.lang.Specification

class TabularFormatterTest extends Specification {

    TabularFormatter f = new TabularFormatter()

    def "formats notes"() {
        def date = new Date(1483570800000)
        def is = [
                new DefaultImprovement(100, "Fix bug x", "http://issues/100", ["bug"], true),
                new DefaultImprovement(101, "New feature", "http://issues/101", [], true)
        ]
        def contributions = new DefaultContributionSet({false} as Predicate)
            .add(new GitCommit("", "a@a", "A", "fixed bug #100"))
            .add(new GitCommit("", "a@a", "A", "refactoring"))
            .add(new GitCommit("", "b@b", "B", "added new feature #101"))

        def contributors = new DefaultContributorsSet()

        def data = new DefaultReleaseNotesData("2.0.1", date, contributions, is, contributors, "v2.0.0", "v2.0.1")

        when: def notes = f.formatVersion(data)
        then: notes == """### 2.0.1 (2017-01-04 23:00 UTC)"""
    }
}
