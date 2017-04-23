package org.mockito.release.notes.contributors

import org.mockito.release.notes.vcs.DefaultContribution
import org.mockito.release.notes.vcs.GitCommit
import spock.lang.Specification
import spock.lang.Subject

class GitHubLastContributorsFetcherTest extends Specification {

    @Subject fetcher = new GitHubLastContributorsFetcher()

    def readOnlyToken = "a0a4c0f41c200f7c653323014d6a72a127764e17"

    def "fetches contributors from GitHub"() {
        def contributions = new LinkedList<DefaultContribution>()
        contributions.add(new DefaultContribution(new GitCommit(
                "1", "continuous.delivery.drone@gmail.com", "Continuous Delivery Drone", "msg 1")))
        contributions.add(new DefaultContribution(new GitCommit(
                "2", "szczepiq@gmail.com", "Szczepan Faber", "msg 2")))

        when:
        def contributors = fetcher.fetchContributors("mockito/mockito", readOnlyToken, contributions, "", "HEAD")

        then:
        contributors.findByAuthorName("Continuous Delivery Drone").login == "continuous-delivery-drone"
        contributors.findByAuthorName("Continuous Delivery Drone").name == "Continuous Delivery Drone"
        contributors.findByAuthorName("Continuous Delivery Drone").profileUrl == "https://github.com/continuous-delivery-drone"
        contributors.findByAuthorName("Szczepan Faber").login == "szczepiq"
        contributors.findByAuthorName("Szczepan Faber").name == "Szczepan Faber"
        contributors.findByAuthorName("Szczepan Faber").profileUrl == "https://github.com/szczepiq"
    }

    def "dont fetch contributors when empty contributions"() {
        when:
        def contributors = fetcher.fetchContributors("mockito/mockito", readOnlyToken, Collections.emptyList(), "", "HEAD")

        then:
        contributors.size() == 0
    }
}
