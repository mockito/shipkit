package org.shipkit.internal.gradle.git

import org.shipkit.gradle.ReleaseConfiguration
import spock.lang.Specification

import static org.shipkit.internal.gradle.git.GitPush.gitPushArgs

class GitPushTest extends Specification {

    ReleaseConfiguration conf
    ReleaseConfiguration.GitHub gitHubConf

    void setup(){
        conf = Mock(ReleaseConfiguration)
        gitHubConf = Mock(ReleaseConfiguration.GitHub)
        conf.getGitHub() >> gitHubConf
    }

    def "git push args"() {
        expect:
        gitPushArgs("http://g.com", ["master", "v1.0"], false) == ["git", "push", "http://g.com", "master", "v1.0"]
        gitPushArgs("http://g.com", ["master", "v1.0"], true) == ["git", "push", "http://g.com", "master", "v1.0", "--dry-run"]
    }
}
