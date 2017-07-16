package org.shipkit.internal.gradle.versionupgrade

import spock.lang.Specification
import spock.lang.Unroll

class DependencyNewVersionParserTest extends Specification {

    DependencyNewVersionParser parser

    def "should parse a valid dependencyNewVersion"() {
        given:
        parser = new DependencyNewVersionParser("SHIP-kit_group1:SHIP-kit_parser1:0.1.2")

        expect:
        parser.valid
        parser.dependencyGroup == "SHIP-kit_group1"
        parser.dependencyName == "SHIP-kit_parser1"
        parser.newVersion == "0.1.2"
    }

    @Unroll
    def "should identify invalid dependencyNewVersion"() {
        given:
        parser = new DependencyNewVersionParser("1.2.3")

        expect:
        !parser.valid
    }
}
