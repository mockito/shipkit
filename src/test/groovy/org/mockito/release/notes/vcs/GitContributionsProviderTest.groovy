package org.mockito.release.notes.vcs

import spock.lang.Specification
import spock.lang.Subject

class GitContributionsProviderTest extends Specification {

    def logProvider = Mock(GitLogProvider)
    @Subject provider = new GitContributionsProvider(logProvider, new IgnoreCiSkip())

    def log = """a5797f9e6cfc06e2fa70ed12ee6c9571af8a7fc9@@info@@szczepiq@gmail.com@@info@@Szczepan Faber@@info@@Tidy-up in buildSrc
next line
@@commit@@
b9d694f4c25880d9dda21ac216053f2bd0f5673c@@info@@szczepiq@gmail.com@@info@@Szczepan Faber@@info@@Tidy-up in buildSrc - started using an interface where possible
@@commit@@
c76924d41c219f3b71b50a28d80c23c9c81b7a8c@@info@@john@doe@@info@@John R. Doe@@info@@dummy commit
@@commit@@"""

    def "provides contributions"() {
        logProvider.getLog("v1.10.10", "HEAD", "--pretty=format:%H@@info@@%ae@@info@@%an@@info@@%B%N@@commit@@") >> log

        when:
        def c = provider.getContributionsBetween("v1.10.10", "HEAD")

        then:
        def commits = c.allCommits as List
        commits[0].commitId == "a5797f9e6cfc06e2fa70ed12ee6c9571af8a7fc9"
        commits[0].authorName == "Szczepan Faber"
        commits[0].authorEmail == "szczepiq@gmail.com"
        commits[0].message == "Tidy-up in buildSrc\nnext line"
    }

    def "has basic handling of garbage in log"() {
        logProvider.getLog(_, _, _) >> (log + " some garbage \n@@commit@@\n more garbage")

        when:
        def c = provider.getContributionsBetween("v1.10.10", "HEAD")

        then:
        c.allCommits.size() == 3
    }

    def "handles empty log"() {
        logProvider.getLog(_, _, _) >> ""

        when:
        def c = provider.getContributionsBetween("v1.10.10", "HEAD")

        then:
        c.allCommits.isEmpty()
    }

    def "should skip ci commits"() {
        def logWithSkipCiCommits = log + """11197f9e6cfc06e2fa70ed12ee6c9571af8a7fc9@@info@@szczepiq@gmail.com@@info@@Szczepan Faber@@info@@[ci skip]sample message
second line
@@commit@@"""
        logProvider.getLog(_, _, _) >> logWithSkipCiCommits

        when:
        def c = provider.getContributionsBetween("v1.10.10", "HEAD")

        then:
        c.allCommits.size() == 3
    }
}
