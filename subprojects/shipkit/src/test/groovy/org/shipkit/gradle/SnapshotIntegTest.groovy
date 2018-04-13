package org.shipkit.gradle

import testutil.GradleSpecification

class SnapshotIntegTest extends GradleSpecification {

    def "snapshot build"() {
        given:
        settingsFile << "include 'foo-module'"
        file("foo-module/build.gradle") << "apply plugin: 'java'"
        buildFile << "apply plugin: 'org.shipkit.java'"
        file("version.properties") << "version=1.0.0"

        when:
        def result = pass("snapshot")

        then:
        println result.output
    }
}
