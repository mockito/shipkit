package org.shipkit.internal.notes.contributors

import spock.lang.Specification

class IgnoredContributorTest extends Specification {

    def "should ignore contributor from ignored list by login"() {
        def ignoredContributor = IgnoredContributor.of(["ignoredContributor"])
        def contributor = new DefaultProjectContributor("name", "ignoredContributor", "profileUrl", 1)

        expect:
        ignoredContributor.test(contributor)
    }

    def "should ignore contributor from ignored list by name"() {
        def ignoredContributor = IgnoredContributor.of(["ignoredContributorName"])
        def contributor = new DefaultProjectContributor("ignoredContributorName", "ignoredContributor", "profileUrl",
            1)

        expect:
        ignoredContributor.test(contributor)
    }

    def "valid contributor when not on the ignored list"() {
        def ignoredContributor = IgnoredContributor.of(["ignoredContributor"])
        def contributor = new DefaultProjectContributor("notIgnoredName", "notIgnoredContributor", "profileUrl", 1)

        expect:
        !ignoredContributor.test(contributor)
    }

    def "valid contributor when empty ignored list"() {
        def ignoredContributor = IgnoredContributor.none()
        def contributor = new DefaultProjectContributor("name", "notIgnoredContributor", "profileUrl", 1)

        expect:
        !ignoredContributor.test(contributor)
    }

    def "should ignore name from ignored list"() {
        def ignoredContributor = IgnoredContributor.of(["ignoredContributor"])

        expect:
        ignoredContributor.test("ignoredContributor")
    }

    def "valid name when not on the ignored list"() {
        def ignoredContributor = IgnoredContributor.of(["ignoredContributor"])

        expect:
        !ignoredContributor.test("notIgnoredContributor ")
    }

    def "valid name when empty ignored list "() {
        def ignoredContributor = IgnoredContributor.none()

        expect:
        !ignoredContributor.test("notIgnoredContributor")
    }
}
