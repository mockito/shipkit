package org.shipkit.internal.gradle.util

import org.gradle.api.GradleException
import org.shipkit.gradle.git.GitPushTask
import spock.lang.Specification
import spock.lang.Unroll

class CannotPushToGithubExceptionTest extends Specification {

    def "should throw proper message for lack of GH_WRITE_TOKEN"() {
        given:
        GitPushTask gitPushTask = Mock(GitPushTask)
        Exception originalException = new RuntimeException("Exception message")
        when:
        def result = CannotPushToGithubException.create(originalException, gitPushTask)
        then:
        result.message == CannotPushToGithubException.GH_WRITE_TOKEN_NOT_SET_MSG
    }

    def "should throw proper message for invalid of GH_WRITE_TOKEN"() {
        given:
        GitPushTask gitPushTask = Mock(GitPushTask)
        gitPushTask.getSecretValue() >> "fake-token"
        Exception originalException = new RuntimeException("Exception message")
        when:
        def result = CannotPushToGithubException.create(originalException, gitPushTask)
        then:
        result.message == CannotPushToGithubException.GH_WRITE_TOKEN_INVALID_MSG
    }

    @Unroll
    def "should match exception: #shouldMatch for message '#message'"() {
        given:
        GradleException ex = Mock(GradleException)
        ex.getMessage() >> message

        expect:
        CannotPushToGithubException.matchException(ex) == shouldMatch

        where:
        message | shouldMatch
        "sth Authentication failed sth" | true
        "sth unable to access sth"      | true
        "other message"                 | false
    }

}
