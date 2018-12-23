package org.shipkit.internal.notes.format

import spock.lang.Specification

class BadgeFormatterTest extends Specification {

    def badgeFormatter = new BadgeFormatter()

    def "bintray badge"() {
        def summary = badgeFormatter.getRepositoryBadge("1.2.3",
            "https://bintray.com/shipkit/",
            "")

        expect:
        summary == """[![Bintray](https://img.shields.io/badge/Bintray-1.2.3-green.svg)](https://bintray.com/shipkit/1.2.3)"""
    }

    def "gradle plugin portal badge"() {
        def summary = badgeFormatter.getRepositoryBadge("1.2.3",
            "https://plugins.gradle.org/plugin/org.shipkit.java/",
            "org.shipkit.java.gradle.plugin")

        expect:
        summary == """[![Gradle](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/org/shipkit/java/org.shipkit.java.gradle.plugin/maven-metadata.xml.svg?colorB=007ec6&label=Gradle)](https://plugins.gradle.org/plugin/org.shipkit.java/1.2.3)"""
    }

    def "gradle plugin portal badge when empty plugin name"() {
        def summary = badgeFormatter.getRepositoryBadge("1.2.3",
            "https://plugins.gradle.org/plugin/org.shipkit.java/",
            "")

        expect:
        summary == """[![Gradle](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/org/shipkit/java/maven-metadata.xml.svg?colorB=007ec6&label=Gradle)](https://plugins.gradle.org/plugin/org.shipkit.java/1.2.3)"""
    }
}
