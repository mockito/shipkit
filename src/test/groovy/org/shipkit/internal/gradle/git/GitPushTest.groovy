package org.shipkit.internal.gradle.git

import org.shipkit.gradle.ShipkitConfiguration
import org.shipkit.gradle.git.GitPushTask
import spock.lang.Specification

import static org.shipkit.internal.gradle.git.GitPush.gitPushArgs
import static org.shipkit.internal.gradle.git.GitPush.setPushUrl

class GitPushTest extends Specification {

    ShipkitConfiguration conf
    ShipkitConfiguration.GitHub gitHubConf

    void setup(){
        conf = Mock(ShipkitConfiguration)
        gitHubConf = Mock(ShipkitConfiguration.GitHub)
        conf.getGitHub() >> gitHubConf
    }

    def "push url with write token"() {
        GitPushTask task = Mock(GitPushTask)
        gitHubConf.getWriteAuthUser() >> "dummy"
        gitHubConf.getWriteAuthToken() >> "secret"
        gitHubConf.getRepository() >> "repo"

        when:
        setPushUrl(task, conf)

        then:
        1 * task.setUrl("https://dummy:secret@github.com/repo.git")
        1 * task.setSecretValue("secret")
    }

    def "push url without write token"() {
        GitPushTask task = Mock(GitPushTask)
        gitHubConf.getRepository() >> "repo"
        gitHubConf.getWriteAuthToken() >> null

        when:
        setPushUrl(task, conf)

        then:
        1 * task.setUrl("https://github.com/repo.git")
        0 * task.setSecretValue(_)
    }

    def "git push args"() {
        expect:
        gitPushArgs("http://g.com", ["master", "v1.0"], false) == ["git", "push", "http://g.com", "master", "v1.0"]
        gitPushArgs("http://g.com", ["master", "v1.0"], true) == ["git", "push", "http://g.com", "master", "v1.0", "--dry-run"]
    }
}
