package org.shipkit.internal.gradle.versionupgrade

import spock.lang.Specification

class DependencyNewVersionParserTest extends Specification {

    DependencyNewVersionParser parser

    def "should parse a valid dependencyNewVersion"() {
        given:
        parser = new DependencyNewVersionParser("SHIP-kit_group1:SHIP-kit_parser1:0.1.2")

        expect:
        def versionUpgrade = new VersionUpgradeConsumerExtension()
        parser.fillVersionUpgradeExtension(versionUpgrade)
        versionUpgrade.dependencyGroup == "SHIP-kit_group1"
        versionUpgrade.dependencyName == "SHIP-kit_parser1"
        versionUpgrade.newVersion == "0.1.2"
    }

    def "should throw exception when invalid dependencyNewVersion"() {
        given:
        parser = new DependencyNewVersionParser("1.2.3")

        when:
        parser.fillVersionUpgradeExtension(new VersionUpgradeConsumerExtension())

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "  Incorrect format of property 'dependency', it should match the pattern " +
            "'[A-Za-z0-9.\\-_]+:[A-Za-z0-9.\\-_]+:[0-9.]+', eg. 'org.shipkit:shipkit:1.2.3'."
    }

    def "should leave versionUpgrade empty when dependencyNewVersion = null"() {
        given:
        parser = new DependencyNewVersionParser(null)

        when:
        def versionUpgrade = new VersionUpgradeConsumerExtension()
        parser.fillVersionUpgradeExtension(versionUpgrade)

        then:
        versionUpgrade.dependencyGroup == null
        versionUpgrade.dependencyName == null
        versionUpgrade.newVersion == null
    }
}
