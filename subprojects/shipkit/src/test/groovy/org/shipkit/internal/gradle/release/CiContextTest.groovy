package org.shipkit.internal.gradle.release

import org.shipkit.internal.util.EnvVariables
import spock.lang.Specification

class CiContextTest extends Specification {

    def "isCiBuild should return true if CI env variable is 'true'"() {
        given:
        def envVars = Mock(EnvVariables)
        def ciEnv = new CiContext(envVars)
        envVars.getNonEmptyEnv("CI") >> "true"

        expect:
        ciEnv.ciBuild
    }

    def "isCiBuild should return false if CI env variable is null"() {
        given:
        def envVars = Mock(EnvVariables)
        def ciEnv = new CiContext(envVars)
        envVars.getNonEmptyEnv("CI") >> null

        expect:
        !ciEnv.ciBuild
    }
}
