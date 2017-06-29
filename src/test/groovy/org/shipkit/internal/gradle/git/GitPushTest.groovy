package org.shipkit.internal.gradle.git

import org.shipkit.gradle.ReleaseConfiguration
import org.shipkit.gradle.git.GitPushTask
import spock.lang.Specification

import static org.shipkit.internal.gradle.git.GitPush.getWriteToken
import static org.shipkit.internal.gradle.git.GitPush.gitPushArgs
import static org.shipkit.internal.gradle.git.GitPush.setPushUrl

class GitPushTest extends Specification {

    def conf = new ReleaseConfiguration()

    def "uses token explicitly configured"() {
        conf.gitHub.writeAuthToken = "secret"
        expect:
        getWriteToken(conf, "secret from env") == "secret"
    }

    def "uses token from env"() {
        expect:
        getWriteToken(conf, "secret from env") == "secret from env"
    }

    def "no token"() {
        expect:
        getWriteToken(conf, null) == null
    }

    def "push url with write token"() {
        GitPushTask task = Mock(GitPushTask)
        conf.gitHub.repository = "repo"

        when:
        setPushUrl(task, conf, "secret")

        then:
        1 * task.setUrl("https://dummy:secret@github.com/repo.git")
        1 * task.setSecretValue("secret")
        0 * _
    }

    def "push url without write token"() {
        GitPushTask task = Mock(GitPushTask)
        conf.gitHub.repository = "repo"

        when:
        setPushUrl(task, conf, null)

        then:
        1 * task.setUrl("https://github.com/repo.git")
        0 * _
    }

    def "git push args"() {
        expect:
        gitPushArgs("http://g.com", ["master", "v1.0"], false) == ["git", "push", "http://g.com", "master", "v1.0"]
        gitPushArgs("http://g.com", ["master", "v1.0"], true) == ["git", "push", "http://g.com", "master", "v1.0", "--dry-run"]
    }
}
