package org.mockito.release.internal.gradle.util

import org.gradle.api.Project
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

    def "git push" () {
        given:
        def conf = new ReleaseConfiguration()

        conf.build.setBranch("master")
        conf.gitHub.setWriteAuthUser("wwilk")
        conf.gitHub.setWriteAuthToken("token")
        conf.gitHub.setRepository("mockito-release-tools")
        conf.setDryRun(false)

        when:
        def result = GitUtil.getGitPushArgs(conf)

        then:
        result == ["git", "push", "https://wwilk:token@github.com/mockito-release-tools.git", "master"]
    }

    def "git push without --dry-run" () {
        given:
        def conf = new ReleaseConfiguration()

        conf.build.setBranch("master")
        conf.gitHub.setWriteAuthUser("wwilk")
        conf.gitHub.setWriteAuthToken("token")
        conf.gitHub.setRepository("mockito-release-tools")
        conf.setDryRun(false)

        when:
        def result = GitUtil.getGitPushArgs(conf)

        then:
        result == ["git", "push", "https://wwilk:token@github.com/mockito-release-tools.git", "master"]
    }

    def "git push with tag" () {
        given:
        def conf = new ReleaseConfiguration()
        def project = Mock(Project)

        conf.build.setBranch("master")
        conf.gitHub.setWriteAuthUser("wwilk")
        conf.gitHub.setWriteAuthToken("token")
        conf.gitHub.setRepository("mockito-release-tools")
        conf.setDryRun(false)
        project.getVersion() >> "0.0.1"

        when:
        def result = GitUtil.getGitPushArgsWithTag(conf, project)

        then:
        result == ["git", "push", "https://wwilk:token@github.com/mockito-release-tools.git", "master", "v0.0.1"]
    }
}
