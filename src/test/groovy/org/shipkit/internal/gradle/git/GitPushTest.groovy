package org.shipkit.internal.gradle.git

import org.shipkit.gradle.configuration.ShipkitConfiguration
import spock.lang.Specification

import static org.shipkit.internal.gradle.git.GitPush.gitPushArgs

class GitPushTest extends Specification {

    ShipkitConfiguration conf
    ShipkitConfiguration.GitHub gitHubConf

    void setup(){
        conf = Mock(ShipkitConfiguration)
        gitHubConf = Mock(ShipkitConfiguration.GitHub)
        conf.getGitHub() >> gitHubConf
    }

    def "git push args"() {
        expect:
        gitPushArgs("http://g.com", ["master", "v1.0"], false) == ["git", "push", "http://g.com", "master", "v1.0"]
        gitPushArgs("http://g.com", ["master", "v1.0"], true) == ["git", "push", "http://g.com", "master", "v1.0", "--dry-run"]
    }
}
