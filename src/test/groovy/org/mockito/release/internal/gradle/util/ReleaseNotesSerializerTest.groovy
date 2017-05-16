package org.mockito.release.internal.gradle.util

import org.apache.commons.lang.builder.EqualsBuilder
import org.gradle.internal.impldep.org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.mockito.release.notes.internal.DefaultReleaseNotesData
import spock.lang.Specification

class ReleaseNotesSerializerTest extends Specification {

    @Rule TemporaryFolder tmp = new TemporaryFolder()

    ReleaseNotesSerializer serializer

    def setup(){
        def tmpFile = tmp.newFile()
        serializer = new ReleaseNotesSerializer(tmpFile)
    }

    def "should serialize and deserialize file"(){
        def releaseNote = new DefaultReleaseNotesData("0.1",
                new Date(),
                null,
                null,
                "0.0.1",
                "0.2")

        def input = [releaseNote]

        when:
        serializer.serialize(input)
        def result = serializer.deserialize()

        then:
        EqualsBuilder.reflectionEquals(result[0], input[0])
    }

}
