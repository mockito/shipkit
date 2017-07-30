package org.shipkit.internal.gradle.util

import org.gradle.testfixtures.ProjectBuilder
import org.shipkit.gradle.configuration.ShipkitConfiguration
import spock.lang.Specification

class GitUtilTest extends Specification {

    def conf = new ShipkitConfiguration()

    def "tag" () {
        def project = new ProjectBuilder().build()
        project.version = "1.0.0"
        conf.git.tagPrefix = "v"

        expect:
        GitUtil.getTag(conf, project) == "v1.0.0"
    }

    def "generic user notation" () {
        def project = new ProjectBuilder().build()
        project.version = "1.0.0"
        conf.git.user = "shipkit-bot"
        conf.git.email = "shipkit.org@gmail.com"

        GitUtil.getGitGenericUserNotation(conf)

        expect:
        GitUtil.getGitGenericUserNotation(conf) == "shipkit-bot <shipkit.org@gmail.com>"
    }

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
