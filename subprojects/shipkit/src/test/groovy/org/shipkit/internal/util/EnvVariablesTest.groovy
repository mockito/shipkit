package org.shipkit.internal.util

import spock.lang.Specification

class EnvVariablesTest extends Specification {

    def "gets non empty"() {
        expect:
        new EnvVariables().getNonEmpty("") == null
    }
}
