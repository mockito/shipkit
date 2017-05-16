package org.mockito.release.notes.vcs

import org.apache.commons.lang.builder.EqualsBuilder
import spock.lang.Specification
import spock.lang.Subject

class DefaultContributionSetSerializerTest extends Specification {
    GitCommitSerializer commitSerializer = Mock(GitCommitSerializer)
    @Subject serializer = new DefaultContributionSetSerializer(commitSerializer)

    def "should serialize and deserialize default contribution set"() {
        def firstCommit = new GitCommit("firstCommitId", "sample@email.com", "sampleAuthor", "sampleCommitMessage")
        def secondCommit = new GitCommit("secondCommitId", "sample@email.com", "sampleAuthor", "sampleCommitMessage")
        def defaultContributionSet = new DefaultContributionSet(new IgnoreCiSkip())
        defaultContributionSet.add(firstCommit)
        defaultContributionSet.add(secondCommit)
        commitSerializer.deserialize(_) >>> [firstCommit, secondCommit]

        when:
        def serializedData = serializer.serialize(defaultContributionSet)
        def deserializedData = serializer.deserialize(serializedData)

        then:
        EqualsBuilder.reflectionEquals(defaultContributionSet.getAllCommits(), deserializedData.getAllCommits())
    }

    def "should skip ci commit after deserialization"() {
        def defaultContributionSet = new DefaultContributionSet(new IgnoreCiSkip())
        def serializedData = serializer.serialize(defaultContributionSet)
        def deserializedData = serializer.deserialize(serializedData)
        def normalCommit = new GitCommit("firstCommitId", "sample@email.com", "sampleAuthor", "sampleCommitMessage")
        def ciSkipCommit = new GitCommit("firstCommitId", "sample@email.com", "sampleAuthor", "[ci skip] sample message")

        when:
        deserializedData.add(normalCommit)
        deserializedData.add(ciSkipCommit)

        then:
        deserializedData.getAllCommits().size() == 1
    }
}
