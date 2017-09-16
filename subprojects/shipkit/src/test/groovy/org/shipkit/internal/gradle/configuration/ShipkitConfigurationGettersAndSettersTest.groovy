package org.shipkit.internal.gradle.configuration

import org.shipkit.gradle.configuration.ShipkitConfiguration
import spock.lang.Shared
import spock.lang.Specification
import testutil.ReflectionUtil

import java.lang.reflect.Method

class ShipkitConfigurationGettersAndSettersTest extends Specification {

    @Shared
    def conf = new ShipkitConfiguration()
    @Shared
    int counter = 0

    def "default values"() {
        conf.team.developers.empty
        conf.team.contributors.empty
        conf.git.commitMessagePostfix == "[ci skip]"
        conf.releaseNotes.ignoreCommitsContaining == ["[ci skip]"]
    }

    def "value assigned to #setter.name should be returned by #getter.name"(row, Method setter, Method getter, Object obj) {
        def valueForSetter = getValueForSetter(setter)
        setter.invoke(obj, valueForSetter)

        expect:
        getter.invoke(obj) == valueForSetter

        where:
        row << ReflectionUtil.findGettersAndSetters(conf)
        setter = row.setter
        getter = row.getter
        obj = row.object
    }

    def getValueForSetter(Method setter) {
        counter++;
        if (setter.parameters[0].type == String.class) {
            return 'some string' + counter
        }

        if (setter.parameters[0].type.name == "boolean") {
            return (counter % 2 == 0);
        }

        if (setter.parameters[0].type == Map.class) {
            def emptyMap = [:]
            emptyMap.put('key' + counter, 'value' + +counter)
            return emptyMap
        }

        if (setter.parameters[0].type == Collection.class) {
            def collection = []
            collection << 'anicos:Adrian Nicos' + counter
            return collection
        }

        throw new RuntimeException("Not supported field")
    }
}
