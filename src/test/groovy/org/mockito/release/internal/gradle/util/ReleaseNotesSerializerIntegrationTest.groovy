package org.mockito.release.internal.gradle.util

import org.mockito.release.notes.internal.DefaultImprovement
import org.mockito.release.notes.internal.DefaultReleaseNotesData
import org.mockito.release.notes.vcs.DefaultContributionSet
import org.mockito.release.notes.vcs.GitCommit
import spock.lang.Specification

class ReleaseNotesSerializerIntegrationTest extends Specification {

    def serializer = new ReleaseNotesSerializer()

    def "should serialize and deserialize whole release notes"() {
        given:
        def improvements = [
                new DefaultImprovement(10123, "Fix bug #123", "https://github.com/org/project/pull/10123", ["noteworthy"], true),
                new DefaultImprovement(10456, "Fix bug #456 and #789", "https://github.com/org/project/pull/10456", [], false),
                new DefaultImprovement(10789, "Refactoring of something", "https://github.com/org/project/pull/10789", ["refactoring"], true)
        ]
        def contributions = new DefaultContributionSet()
        contributions.add(new GitCommit("123", "aaa@example.com", "aaa", "Fix bug #123"))
        contributions.add(new GitCommit("456", "bbb@example.com", "bbb", "Fix bug #456 and #789"))
        contributions.add(new GitCommit("789", "ccc@example.com", "ccc", "Refactoring"))
        def releaseNote = new DefaultReleaseNotesData("1.2.3",
                new Date(1495668226000),
                contributions,
                improvements,
                "0.3.5",
                "0.3.6")
        def releaseNotes = [releaseNote]

        when:
        def serializedJson = serializer.serialize(releaseNotes)
        def result = serializer.deserialize(serializedJson)

        then:
        result.get(0) == releaseNote
    }
}
