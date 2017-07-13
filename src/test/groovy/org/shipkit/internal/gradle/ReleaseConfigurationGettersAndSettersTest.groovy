package org.shipkit.internal.gradle

import org.shipkit.gradle.ReleaseConfiguration
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import testutil.ReflectionUtil

import java.lang.reflect.Method

class ReleaseConfigurationGettersAndSettersTest extends Specification {

    @Shared
    def conf = new ReleaseConfiguration()

    def "default values"() {
        conf.team.developers.empty
        conf.team.contributors.empty
        conf.git.commitMessagePostfix == "[ci skip]"
        conf.releaseNotes.ignoreCommitsContaining == ["[ci skip]"]
    }

    @Unroll
    def "setter #setter.name value should be the same like #getter.name"(row, Method setter, Method getter, Object obj) {
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
        if (setter.parameters[0].type == String.class) {
            return "some string"
        }

        if (setter.parameters[0].type.name == "boolean") {
            return true;
        }

        if (setter.parameters[0].type == Map.class) {
            def emptyMap = [:]
            emptyMap.put("key", "value")
            return emptyMap
        }

        if (setter.parameters[0].type == Collection.class) {
            def collection = []
            collection << "anicos:Adrian Nicos"
            return collection
        }

        throw new RuntimeException("Not supported field")
    }
}
