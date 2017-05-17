package org.mockito.release.notes.vcs

import spock.lang.Specification
import spock.lang.Subject

class DefaultContributionSetTest extends Specification {

    @Subject contributions = new DefaultContributionSet()

    def "empty contributions"() {
        expect:
        contributions.allCommits.isEmpty()
        contributions.allTickets.isEmpty()
    }

    def "contains referenced tickets"() {
        contributions.add(new GitCommit("", "a@x", "A", "fixes issue #123"))
        contributions.add(new GitCommit("", "a@x", "A", "fixes issue 250 and #123"))
        contributions.add(new GitCommit("", "b@x", "B", """adds new feature
#100
"""))

        expect:
        contributions.allTickets == ["123", "100"] as Set
    }
}
