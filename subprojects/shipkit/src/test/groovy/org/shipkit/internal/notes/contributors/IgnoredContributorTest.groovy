package org.shipkit.internal.notes.contributors

import spock.lang.Specification

class IgnoredContributorTest extends Specification {

    def "should ignore contributor from ignored list"() {
        def ignoredContributor = IgnoredContributor.of(["ignoredContributor"])
        def contributor = new DefaultProjectContributor("name", "ignoredContributor", "profileUrl", 1)

        expect:
        ignoredContributor.isTrue(contributor)
    }

    def "valid contributor when not on the ignored list"() {
        def ignoredContributor = IgnoredContributor.of(["ignoredContributor"])
        def contributor = new DefaultProjectContributor("name", "notIgnoredContributor", "profileUrl", 1)

        expect:
        !ignoredContributor.isTrue(contributor)
    }

    def "valid contributor when empty ignored list"() {
        def ignoredContributor = IgnoredContributor.none()
        def contributor = new DefaultProjectContributor("name", "notIgnoredContributor", "profileUrl", 1)

        expect:
        !ignoredContributor.isTrue(contributor)
    }
}
