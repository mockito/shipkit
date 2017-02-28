package org.mockito.release.notes.improvements

import org.mockito.release.notes.format.DefaultFormatter
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Subject

@Ignore
class GitHubTicketFetcherTest extends Specification {

    @Subject fetcher = new GitHubTicketFetcher()

    //This is an integration test
    //It's not ideal but it gives us a good smoke test
    //So far it is not problematic to maintain :)
    def "fetches improvements from GitHub"() {
        def readOnlyToken = "a0a4c0f41c200f7c653323014d6a72a127764e17"
        when:
        def improvements = fetcher.fetchTickets(readOnlyToken, ['109', '108', '99999', '112'], [], false) as List

        then:
        //TODO SF we can leave this test but we should create a sample project for it instead of using Mockito repo
        //This way it will be faster, mockito repo has many issues and pagination takes a lot of time
        improvements[0].labels == ["enhancement"] as Set
        new DefaultFormatter([:]).format([:], improvements) == """* Improvements: 3
  * Allow instances of other classes in AdditionalAnswers.delegatesTo [(#112)](https://github.com/mockito/mockito/issues/112)
  * Improve automated release notes look [(#109)](https://github.com/mockito/mockito/issues/109)
  * Clarify Spy vs Mock CALLS_REAL_METHODS [(#108)](https://github.com/mockito/mockito/issues/108)"""
    }
}
