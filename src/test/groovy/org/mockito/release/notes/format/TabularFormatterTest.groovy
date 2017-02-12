package org.mockito.release.notes.format

import org.mockito.release.notes.DefaultVersionNotesData
import org.mockito.release.notes.improvements.DefaultImprovement
import org.mockito.release.notes.util.Predicate
import org.mockito.release.notes.vcs.DefaultContributionSet
import org.mockito.release.notes.vcs.GitCommit
import spock.lang.Specification

class TabularFormatterTest extends Specification {

    TabularFormatter f = new TabularFormatter()

    def "formats notes"() {
        def date = new Date(1483570800000)
        def is = [
            new DefaultImprovement(100, "Fix bug x", "http://issues/100",   ["bug"]),
            new DefaultImprovement(101, "New feature", "http://issues/101", [])
        ]
        def contributions = new DefaultContributionSet({false} as Predicate)
            .add(new GitCommit("a@a", "A", "fixed bug #100"))
            .add(new GitCommit("a@a", "A", "refactoring"))
            .add(new GitCommit("b@b", "B", "added new feature #101"))

        def data = new DefaultVersionNotesData("2.0.1", date, contributions, is)

        when: def notes = f.formatVersion(data)
        then: notes == """### 2.0.1 (2017-01-04 23:00 UTC)"""
    }
}
