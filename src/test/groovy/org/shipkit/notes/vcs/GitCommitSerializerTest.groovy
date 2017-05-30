package org.shipkit.notes.vcs

import org.apache.commons.lang.builder.EqualsBuilder
import spock.lang.Specification
import spock.lang.Subject

class GitCommitSerializerTest extends Specification {
    @Subject serializer = new GitCommitSerializer()

    def "should serialize and deserialize git commit data"() {
        def gitCommit = new GitCommit("sampleId", "sample@email.com", "sampleAuthor", "sampleCommitMessage")

        when:
        def serializedData = serializer.serialize(gitCommit)
        def deserializedData = serializer.deserialize(serializedData)

        then:
        EqualsBuilder.reflectionEquals(gitCommit, deserializedData)
    }

    def "should serialize and deserialize git commit data with special characters"() {
        def gitCommit = new GitCommit("sample\\Id", "sample@ema\"il.com", "sample\u0000Author", "sample\"Commit\"Message")

        when:
        def serializedData = serializer.serialize(gitCommit)
        def deserializedData = serializer.deserialize(serializedData)

        then:
        EqualsBuilder.reflectionEquals(gitCommit, deserializedData)
    }
}
