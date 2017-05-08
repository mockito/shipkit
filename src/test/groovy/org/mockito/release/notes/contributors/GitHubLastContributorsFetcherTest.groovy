package org.mockito.release.notes.contributors

import spock.lang.Specification
import spock.lang.Subject

class GitHubLastContributorsFetcherTest extends Specification {

    @Subject fetcher = new GitHubLastContributorsFetcher()

    def readOnlyToken = "a0a4c0f41c200f7c653323014d6a72a127764e17"

    def "fetches contributors from GitHub"() {
        when:
        def c = fetcher.fetchContributors("mockito/mockito", readOnlyToken, "2017-05-04", "2017-05-06")

        then:
        c.toString() == "[Roman Elizarov/elizarov, Allon Murienik/mureinik]"
    }

    def "no contributors for given dates"() {
        def c = fetcher.fetchContributors("mockito/mockito", readOnlyToken, "2017-05-01", "2017-05-03")
        expect:
        c.empty
    }

    def "null until date smoke test"() {
        def c = fetcher.fetchContributorsSinceYesterday("mockito/mockito-release-tools", readOnlyToken)
        expect:
        //we cannot write assertions because we are querying for commits since yesterday
        //and the commits can change. Smoke testing only
        println c*.profileUrl
    }
}
