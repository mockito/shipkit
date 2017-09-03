package org.shipkit.internal.gradle.configuration

import org.gradle.api.GradleException
import spock.lang.Specification

class BasicValidatorTest extends Specification {

    def "not null"() {
        expect:
        BasicValidator.notNull("all", "good")

        when:
        BasicValidator.notNull(null, "not good")

        then:
        def e = thrown(GradleException)
        e.message == "not good"
    }

    def "not null with env variable"() {
        expect:
        BasicValidator.notNull("all", "SOME_ENV", "good")
        BasicValidator.notNullEnv("SOME_ENV", "some value", "good")
    }

    def "not null with no env variable value"() {
        when:
        BasicValidator.notNullEnv("SOME_ENV", null, "not good")

        then:
        def e = thrown(GradleException)
        e.message == "not good"
    }
}
