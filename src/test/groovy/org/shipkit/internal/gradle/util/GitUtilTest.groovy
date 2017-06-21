package org.shipkit.internal.gradle.util

import org.shipkit.gradle.ReleaseConfiguration
import spock.lang.Specification

class GitUtilTest extends Specification {

    def conf = new ReleaseConfiguration()

    def "commit message" () {
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
