package org.shipkit.internal.notes.internal

import org.apache.commons.lang.builder.EqualsBuilder
import spock.lang.Specification
import spock.lang.Subject

class DefaultImprovementSerializerTest extends Specification {

    @Subject serializer = new DefaultImprovementSerializer()

    def "should serialize and deserialize default improvement"() {
        def improvement = new DefaultImprovement(123L, "sampleTitle", "sample.url.com", Arrays.asList("firstLabel", "secondLabel"), true)

        when:
        def serializedData = serializer.serialize(improvement)
        def deserializedData = serializer.deserialize(serializedData)

        then:
        EqualsBuilder.reflectionEquals(improvement, deserializedData)
    }

    def "should serialize and deserialize git commit data with special characters"() {
        def improvement = new DefaultImprovement(123L, "samp\"leTitle", "sampl\u0000e.u\"rl.com", Arrays.asList("fi\"r\u0000stLabel", "seco\u0000ndLabel"), false)

        when:
        def serializedData = serializer.serialize(improvement)
        def deserializedData = serializer.deserialize(serializedData)

        then:
        EqualsBuilder.reflectionEquals(improvement, deserializedData)
    }
}
