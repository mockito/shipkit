package org.shipkit.internal.notes.contributors.github

import spock.lang.Specification
import spock.lang.Subject

import static org.shipkit.internal.util.DateUtil.parseDate

class RecentContributorsFetcherTest extends Specification {

    @Subject fetcher = new RecentContributorsFetcher()

    def readOnlyToken = "a0a4c0f41c200f7c653323014d6a72a127764e17"
    def defaultGitHubApiEndpoint = "https://api.github.com";

    def "fetches contributors from GitHub"() {
        when:
        def c = fetcher.fetchContributors(defaultGitHubApiEndpoint, "mockito/mockito", readOnlyToken,
                parseDate("2017-05-04 00:00:00 -0000"), parseDate("2017-05-06 00:00:00 -0000"))

        then:
        c.toString() == "[Roman Elizarov/elizarov, Szczepan Faber/szczepiq]"
    }

    def "no contributors for given dates"() {
        def c = fetcher.fetchContributors(defaultGitHubApiEndpoint, "mockito/mockito", readOnlyToken,
                parseDate("2017-05-02 00:00:00 -0000"), parseDate("2017-05-03 00:00:00 -0000"))
        expect:
        c.empty
    }

    def "null until date smoke test"() {
        def c = fetcher.fetchContributorsSinceYesterday(defaultGitHubApiEndpoint, "mockito/shipkit", readOnlyToken)
        expect:
        //we cannot write assertions because we are querying for commits since yesterday
        //and the commits can change. Smoke testing only
        println c*.profileUrl
    }
}
