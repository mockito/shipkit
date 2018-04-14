package org.shipkit.gradle

import org.gradle.testkit.runner.TaskOutcome
import testutil.GradleSpecification

class SnapshotIntegTest extends GradleSpecification {

    def "snapshot build for java submodule"() {
        given:
        settingsFile << "include 'java-module'"
        buildFile << "apply plugin: 'org.shipkit.java'"

        newFile("java-module/build.gradle")   << "apply plugin: 'java'"

        when:
        def result = pass("snapshot")

        then:
        result.task(":java-module:snapshot").outcome == TaskOutcome.SUCCESS
        result.task(":snapshot").outcome == TaskOutcome.UP_TO_DATE //this is how Gradle reports tasks with no behavior
        file("java-module/build/libs/java-module-1.0.0-SNAPSHOT.jar").exists()
    }

    def "snapshot build for Gradle plugin project"() {
        given:
        settingsFile << "include 'gradle-plugin-module'"
        buildFile << "apply plugin: 'org.shipkit.gradle-plugin'"

        newFile("gradle-plugin-module/build.gradle")   << "apply plugin: 'com.gradle.plugin-publish'"

        when:
        def result = pass("snapshot")

        then:
        result.task(":gradle-plugin-module:snapshot").outcome == TaskOutcome.SUCCESS
        result.task(":snapshot").outcome == TaskOutcome.UP_TO_DATE
        file("gradle-plugin-module/build/libs/gradle-plugin-module-1.0.0-SNAPSHOT.jar").exists()
    }
}
