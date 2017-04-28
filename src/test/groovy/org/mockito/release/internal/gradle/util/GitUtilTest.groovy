package org.mockito.release.internal.gradle.util

import org.mockito.release.gradle.ReleaseConfiguration
import spock.lang.Specification

class GitUtilTest extends Specification {

    def "commit message" () {
        def conf = new ReleaseConfiguration()
        conf.git.commitMessagePostfix = postfix

        expect:
        GitUtil.getCommitMessage(conf, info) == message

        where:
        info  | postfix     | message
        "foo" | "[ci skip]" | "foo [ci skip]"
        ""    | "[ci skip]" | " [ci skip]"    //info will never be empty, only documenting behavior
        "foo" | ""          | "foo"
    }
}
