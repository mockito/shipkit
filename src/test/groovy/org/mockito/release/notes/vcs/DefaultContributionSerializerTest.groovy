package org.mockito.release.notes.vcs

import org.apache.commons.lang.builder.EqualsBuilder
import spock.lang.Specification
import spock.lang.Subject

class DefaultContributionSerializerTest extends Specification {
    GitCommitSerializer commitSerializer = Mock(GitCommitSerializer)
    @Subject serializer = new DefaultContributionSerializer(commitSerializer)

    def "should serialize and deserialize default contribution"() {
        def firstCommit = new GitCommit("firstCommitId", "sample@email.com", "sampleAuthor", "sampleCommitMessage")
        def secondCommit = new GitCommit("secondCommitId", "sample@email.com", "sampleAuthor", "sampleCommitMessage")
        def defaultContribution = new DefaultContribution(firstCommit)
        defaultContribution.add(secondCommit)
        commitSerializer.deserialize(_) >>> [firstCommit, secondCommit]

        when:
        def serializedData = serializer.serialize(defaultContribution)
        def deserializedData = serializer.deserialize(serializedData)

        then:
        EqualsBuilder.reflectionEquals(defaultContribution, deserializedData)
    }
}
