package org.shipkit.internal.notes.vcs

import spock.lang.Specification

class IgnoredCommitTest extends Specification {

    def "should skip commits that contains given ignored substrings"() {
        def ignoredCommit = new IgnoredCommit(["[ci skip]", "[custom skip]"], [])
        def commitWithCiSkip = createGitCommitWithMessage("sample [ci skip] commit message")
        def commitWithCustomSkip = createGitCommitWithMessage("sample [custom skip] commit message")
        def commitWithoutSkipSubstringInMessage = createGitCommitWithMessage("sample commit message")

        expect:
        ignoredCommit.isTrue(commitWithCiSkip)
        ignoredCommit.isTrue(commitWithCustomSkip)
        ignoredCommit.isTrue(commitWithoutSkipSubstringInMessage) == false
    }

    def "should not skip commit if ignored substrings list is empty"() {
        def ignoredCommit = new IgnoredCommit([], [])
        def commitWithCiSkip = new GitCommit("firstCommitId", "sample@email.com", "sampleAuthor", "sample [ci skip] commit message")

        expect:
        ignoredCommit.isTrue(commitWithCiSkip) == false
    }

    def "should omit commit done by ignored contributor"() {
        def ignoredCommit = new IgnoredCommit([], ["ignoredContributor"])
        def commit = new GitCommit("comitId", "commit@email.com", "ignoredContributor", "sample message")

        expect:
        ignoredCommit.isTrue(commit) == true
    }

    def "should leave commit when contributor not on the ignored list"() {
        def ignoredCommit = new IgnoredCommit([], ["ignoredContributor"])
        def commit = new GitCommit("commitId", "commit@email.com", "notIgnoredContributor", "sample message")

        expect:
        ignoredCommit.isTrue(commit) == false
    }

    private GitCommit createGitCommitWithMessage(message) {
        new GitCommit("id", "sample@email.com", "sampleAuthor", message)
    }
}
