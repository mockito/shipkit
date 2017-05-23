package org.mockito.release.notes.vcs

import spock.lang.Specification

class CommitIgnoredTest extends Specification {

    def "should skip commits with that contains given ignored substrings"() {
        def commitIgnored = new CommitIgnored(["[ci skip]", "[custom skip]"])
        def commitWithCiSkip = new GitCommit("firstCommitId", "sample@email.com", "sampleAuthor", "sample [ci skip] commit message")
        def commitWithCustomSkip = new GitCommit("thirdCommitId", "sample@email.com", "sampleAuthor", "sample [custom skip] commit message")

        expect:
        commitIgnored.isTrue(commitWithCiSkip)
        commitIgnored.isTrue(commitWithCustomSkip)
    }

    def "should not skip commit that does not contain given ignored substrings"() {
        def commitIgnored = new CommitIgnored(["[ci skip]", "[custom skip]"])
        def commit = new GitCommit("firstCommitId", "sample@email.com", "sampleAuthor", "sample commit message")

        expect:
        commitIgnored.isTrue(commit) == false
    }
}
