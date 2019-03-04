package org.shipkit.internal.notes.format

import spock.lang.Specification

class BadgeFormatterTest extends Specification {

    def badgeFormatter = new BadgeFormatter()

    def "bintray badge"() {
        def summary = badgeFormatter.getRepositoryBadge("1.2.3",
            "https://bintray.com/shipkit/")

        expect:
        summary == """[![Bintray](https://img.shields.io/badge/Bintray-1.2.3-green.svg)](https://bintray.com/shipkit/1.2.3)"""
    }

    def "gradle plugin portal badge"() {
        def summary = badgeFormatter.getRepositoryBadge("1.2.3",
            "https://plugins.gradle.org/plugin/org.shipkit.java/")

        expect:
        summary == """[![Gradle](https://img.shields.io/badge/Gradle-v1.2.3-blue.svg)](https://plugins.gradle.org/plugin/org.shipkit.java/1.2.3)"""
    }
}
