package org.mockito.release.notes.vcs

import spock.lang.Specification
import spock.lang.Subject

class DefaultContributionSetSerializerTest extends Specification {

    @Subject serializer = new DefaultContributionSetSerializer()

    def "should serialize and deserialize default contribution set"() {
        def firstCommit = new GitCommit("firstCommitId", "sample@email.com", "sampleAuthor", "sampleCommitMessage")
        def secondCommit = new GitCommit("secondCommitId", "sample@email.com", "sampleAuthor", "sampleCommitMessage")
        def defaultContributionSet = new DefaultContributionSet()
        defaultContributionSet.add(firstCommit)
        defaultContributionSet.add(secondCommit)

        when:
        def serializedData = serializer.serialize(defaultContributionSet)
        def deserializedData = serializer.deserialize(serializedData)

        then:
        defaultContributionSet.getAllCommits() == deserializedData.getAllCommits()
    }
}
