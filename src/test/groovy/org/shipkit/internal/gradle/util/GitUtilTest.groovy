package org.shipkit.internal.gradle.util

import org.gradle.api.Project
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

    def "git push" () {
        given:
        def project = Mock(Project)

        conf.gitHub.setWriteAuthUser("wwilk")
        conf.gitHub.setWriteAuthToken("token")
        conf.gitHub.setRepository("mockito-release-tools")
        conf.setDryRun(false)
        project.getVersion() >> "0.0.1"

        when:
        def result = GitUtil.getGitPushArgs(conf, project, "master")

        then:
        result == ["git", "push", "https://wwilk:token@github.com/mockito-release-tools.git", "master", "v0.0.1"]
    }

    def "git push with --dry-run" () {
        given:
        def project = Mock(Project)

        conf.gitHub.setWriteAuthUser("wwilk")
        conf.gitHub.setWriteAuthToken("token")
        conf.gitHub.setRepository("mockito-release-tools")
        conf.setDryRun(true)
        project.getVersion() >> "0.0.1"

        when:
        def result = GitUtil.getGitPushArgs(conf, project, "master")

        then:
        result == ["git", "push", "https://wwilk:token@github.com/mockito-release-tools.git", "master", "v0.0.1", "--dry-run"]
    }
}
