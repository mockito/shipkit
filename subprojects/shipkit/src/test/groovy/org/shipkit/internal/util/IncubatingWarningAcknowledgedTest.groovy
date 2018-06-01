package org.shipkit.internal.util

import org.shipkit.gradle.configuration.ShipkitConfiguration
import spock.lang.Specification

class IncubatingWarningAcknowledgedTest extends Specification {

    def "name in acknowledged warnings"(){
        given:
        ShipkitConfiguration configuration = new ShipkitConfiguration()
        configuration.incubatingWarnings.acknowledged = Arrays.asList("test2", "test")
        def incubatingWarningAcknowledged = new IncubatingWarningAcknowledged(configuration)

        expect:
        incubatingWarningAcknowledged.test("test")
    }

    def "name starts with acknowledged entry in acknowledged warnings"(){
        given:
        ShipkitConfiguration configuration = new ShipkitConfiguration()
        configuration.incubatingWarnings.acknowledged = Arrays.asList("test2", "test")
        def incubatingWarningAcknowledged = new IncubatingWarningAcknowledged(configuration)

        expect:
        incubatingWarningAcknowledged.test("test plugin")
    }

    def "no acknowledged incubating warnings"(){
        given:
        ShipkitConfiguration configuration = new ShipkitConfiguration()
        configuration.incubatingWarnings.acknowledged = Collections.emptyList()
        def incubatingWarningAcknowledged = new IncubatingWarningAcknowledged(configuration)

        expect:
        !incubatingWarningAcknowledged.test("test")
    }

    def "name not in acknowledged warnings"(){
        given:
        ShipkitConfiguration configuration = new ShipkitConfiguration()
        configuration.incubatingWarnings.acknowledged = Arrays.asList("not1", "not2")
        def incubatingWarningAcknowledged = new IncubatingWarningAcknowledged(configuration)

        expect:
        !incubatingWarningAcknowledged.test("test")
    }

    def "name with acknowledged text, but does not start with it"(){
        given:
        ShipkitConfiguration configuration = new ShipkitConfiguration()
        configuration.incubatingWarnings.acknowledged = Arrays.asList("test", "ot")
        def incubatingWarningAcknowledged = new IncubatingWarningAcknowledged(configuration)

        expect:
        !incubatingWarningAcknowledged.test("nottest")
    }
}
