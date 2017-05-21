package org.mockito.release.notes.vcs

import spock.lang.Specification

class DefaultCommitApproverTest extends Specification {

    def "should approve commit with sample message"() {
        def approver = new DefaultCommitApprover()
        def commit = new GitCommit("sampleId", "sample@email.com", "sampleAuthor", "sample commit message")

        when:
        def result = approver.isTrue(commit)

        then:
        result == true
    }

    def "should decline commit with [ci skip] text in message"() {
        def approver = new DefaultCommitApprover()
        def commit = new GitCommit("sampleId", "sample@email.com", "sampleAuthor", "commit [ci skip] message")

        when:
        def result = approver.isTrue(commit)

        then:
        result == false
    }

    def "should decline commit with defined postfix in message"() {
        def samplePostfix = "samplePostfix"
        def approver = new DefaultCommitApprover(samplePostfix)
        def commit = new GitCommit("sampleId", "sample@email.com", "sampleAuthor", "commit message" + samplePostfix)

        when:
        def result = approver.isTrue(commit)

        then:
        result == false
    }

    def "should approve commit if configured postfix message is not at the end of commit message"() {
        def samplePostfix = "samplePostfix"
        def approver = new DefaultCommitApprover(samplePostfix)
        def commit = new GitCommit("sampleId", "sample@email.com", "sampleAuthor", "commit " + samplePostfix + " message")

        when:
        def result = approver.isTrue(commit)

        then:
        result == true
    }
}
