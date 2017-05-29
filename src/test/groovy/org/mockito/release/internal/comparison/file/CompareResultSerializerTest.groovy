package org.mockito.release.internal.comparison.file

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Subject


class CompareResultSerializerTest extends Specification {

    @Subject serializer = new CompareResultSerializer()

    @Rule
    TemporaryFolder tmp = new TemporaryFolder()

    File dirA

    def setup() {
        dirA = tmp.newFolder('dirA')
    }

    def "serialization and deserialization of compareResult (emty)"() {
        given:
        def compareResult = new CompareResult()
        compareResult.setOnlyA([])
        compareResult.setOnlyB([])
        compareResult.setBothButDifferent([])

        when:
        def json = serializer.serialize(compareResult)
        def actual = serializer.deserialize(json)

        then:
        actual.onlyA == []
        actual.onlyB == []
        actual.bothButDifferent == []
    }

    def "serialization and deserialization of compareResult (onlyA)"() {
        given:
        def compareResult = new CompareResult()
        compareResult.setOnlyA([new File(dirA, "a"), new File(dirA,"b")])
        compareResult.setOnlyB([])
        compareResult.setBothButDifferent([])

        when:
        def json = serializer.serialize(compareResult)
        def actual = serializer.deserialize(json)

        then:
        actual.onlyA == [new File(dirA, "a"), new File(dirA,"b")]
        actual.onlyB == []
        actual.bothButDifferent == []
    }

    def "serialization and deserialization of compareResult (onlyB)"() {
        given:
        def compareResult = new CompareResult()
        compareResult.setOnlyA([])
        compareResult.setOnlyB([new File(dirA, "a"), new File(dirA,"b")])
        compareResult.setBothButDifferent([])

        when:
        def json = serializer.serialize(compareResult)
        def actual = serializer.deserialize(json)

        then:
        actual.onlyA == []
        actual.onlyB == [new File(dirA, "a"), new File(dirA,"b")]
        actual.bothButDifferent == []
    }

    def "serialization and deserialization of compareResult (both)"() {
        given:
        def compareResult = new CompareResult()
        compareResult.setOnlyA([])
        compareResult.setOnlyB([])
        compareResult.setBothButDifferent([new File(dirA, "a"), new File(dirA,"b")])

        when:
        def json = serializer.serialize(compareResult)
        def actual = serializer.deserialize(json)

        then:
        actual.onlyA == []
        actual.onlyB == []
        actual.bothButDifferent == [new File(dirA, "a"), new File(dirA,"b")]
    }

    def "serialization and deserialization of compareResult (full sample)"() {
        given:
        def compareResult = new CompareResult()
        compareResult.setOnlyA([new File(dirA, "x"), new File(dirA,"y")])
        compareResult.setOnlyB([new File(dirA, "u"), new File(dirA,"v"), new File(dirA,"w")])
        compareResult.setBothButDifferent([new File(dirA, "a"), new File(dirA,"b")])

        when:
        def json = serializer.serialize(compareResult)
        def actual = serializer.deserialize(json)

        then:
        actual.onlyA == [new File(dirA, "x"), new File(dirA,"y")]
        actual.onlyB == [new File(dirA, "u"), new File(dirA,"v"), new File(dirA,"w")]
        actual.bothButDifferent == [new File(dirA, "a"), new File(dirA,"b")]
    }
}
