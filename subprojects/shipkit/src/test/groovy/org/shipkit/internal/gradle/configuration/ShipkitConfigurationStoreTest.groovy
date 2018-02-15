package org.shipkit.internal.gradle.configuration

import org.gradle.api.GradleException
import org.shipkit.internal.util.EnvVariables
import spock.lang.Specification

class ShipkitConfigurationStoreTest extends Specification {

    def envVariables = Mock(EnvVariables)
    def store = new ShipkitConfigurationStore([:], envVariables, false)

    def "should use env variable if value not set explicitly"() {
        given:
        envVariables.getNonEmptyEnv("ENV") >> "some value"

        expect:
        store.getValue("foo", "ENV", "Error!") == "some value"
    }

    def "should override env variable value set explicitly"() {
        given:
        envVariables.getNonEmptyEnv("ENV") >> "some value"
        store.put("foo", "other value")

        expect:
        store.getValue("foo", "ENV", "Error!") == "other value"
    }

    def "should throw exception if value is not present"() {
        when:
        store.getValue("foo", "ENV", "Error!")

        then:
        def ex = thrown(GradleException)
        ex.message == "Error!"
    }

    def "should return null if lenient and value not present"() {
        expect:
        store.lenient.getValue("foo", "ENV", "Error!") == null
    }
}
