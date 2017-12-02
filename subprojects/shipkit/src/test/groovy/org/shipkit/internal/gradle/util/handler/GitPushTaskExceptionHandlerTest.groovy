package org.shipkit.internal.gradle.util.handler

import org.shipkit.internal.gradle.util.handler.exceptions.CannotPushToGithubException
import spock.lang.Specification
import spock.lang.Unroll

class GitPushTaskExceptionHandlerTest extends Specification {

    def "throws original exception because message does not match"() {
        def e = new RuntimeException("foo")

        when:
        new GitPushTaskExceptionHandler(null).execute(e)

        then:
        def ex = thrown(Exception)
        ex == e
    }

    @Unroll def "wraps original exception when message matches"() {
        def e = new RuntimeException(message)

        when:
        new GitPushTaskExceptionHandler(secret).execute(e)

        then:
        def ex = thrown(CannotPushToGithubException)
        ex.cause == e

        where:
        message                         | secret | newMessage
        "sth Authentication failed sth" | null   | GitPushTaskExceptionHandler.GH_WRITE_TOKEN_NOT_SET_MSG
        "sth unable to access sth"      | "asdf" | GitPushTaskExceptionHandler.GH_WRITE_TOKEN_INVALID_MSG
    }
}
