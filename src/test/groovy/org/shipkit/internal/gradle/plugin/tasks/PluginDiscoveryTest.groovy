package org.shipkit.internal.gradle.plugin.tasks

import spock.lang.Specification

class PluginDiscoveryTest extends Specification {

    def "generate plugin name"() {
        expect:
        PluginDiscovery.generatePluginName(input) == expected

        where:
        input                                       | expected
        'plugin.properties'                         | 'plugin'
        'com.shipkit.base-java-library.properties'  | 'baseJavaLibrary'
        'com.shipkit.versioning.properties'         | 'versioning'
    }
}
