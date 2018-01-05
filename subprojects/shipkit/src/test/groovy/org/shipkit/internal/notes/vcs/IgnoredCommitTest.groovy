package org.shipkit.internal.notes.vcs

import org.shipkit.internal.notes.contributors.IgnoredContributor
import spock.lang.Specification

class IgnoredCommitTest extends Specification {

    def "should skip commits that contains given ignored substrings"() {
        def ignoredContributor = Mock(IgnoredContributor)
        ignoredContributor.isTrue("sampleAuthor") >> false
        def ignoredCommit = new IgnoredCommit(["[ci skip]", "[custom skip]"], ignoredContributor)
        def commitWithCiSkip = createGitCommitWithMessage("sample [ci skip] commit message")
        def commitWithCustomSkip = createGitCommitWithMessage("sample [custom skip] commit message")
        def commitWithoutSkipSubstringInMessage = createGitCommitWithMessage("sample commit message")

        expect:
        ignoredCommit.isTrue(commitWithCiSkip)
        ignoredCommit.isTrue(commitWithCustomSkip)
        !ignoredCommit.isTrue(commitWithoutSkipSubstringInMessage)
    }

    def "should keep commit if ignored substrings list is empty"() {
        def ignoredContributor = Mock(IgnoredContributor)
        ignoredContributor.isTrue("sampleAuthor") >> false
        def ignoredCommit = new IgnoredCommit([], ignoredContributor)
        def commitWithCiSkip = new GitCommit("firstCommitId", "sample@email.com", "sampleAuthor", "sample [ci skip] commit message")

        expect:
        !ignoredCommit.isTrue(commitWithCiSkip)
    }

    def "should skip commit done by ignored contributor"() {
        def ignoredContributor = Mock(IgnoredContributor)
        ignoredContributor.isTrue("ignoredContributor") >> true
        def ignoredCommit = new IgnoredCommit([], ignoredContributor)
        def commit = new GitCommit("commitId", "commit@email.com", "ignoredContributor", "sample message")

        expect:
        ignoredCommit.isTrue(commit)
    }

    def "should keep commit when contributor not on the ignored list"() {
        def ignoredContributor = Mock(IgnoredContributor)
        ignoredContributor.isTrue("notIgnoredContributor") >> false
        def ignoredCommit = new IgnoredCommit([], ignoredContributor)
        def commit = new GitCommit("commitId", "commit@email.com", "notIgnoredContributor", "sample message")

        expect:
        !ignoredCommit.isTrue(commit)
    }

    def "should keep commit when contributor not on the ignored list and commit not with ignored message"() {
        def ignoredContributor = Mock(IgnoredContributor)
        ignoredContributor.isTrue("notIgnoredContributor") >> false
        def ignoredCommit = new IgnoredCommit(["ignored message"], ignoredContributor)
        def commit = createGitCommitWithMessage("sample message")

        expect:
        !ignoredCommit.isTrue(commit)
    }

    private GitCommit createGitCommitWithMessage(message) {
        new GitCommit("id", "sample@email.com", "sampleAuthor", message)
    }
}
