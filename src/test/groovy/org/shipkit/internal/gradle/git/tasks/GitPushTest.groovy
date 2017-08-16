package org.shipkit.internal.gradle.git.tasks

import spock.lang.Specification

import static org.shipkit.internal.gradle.git.tasks.GitPush.gitPushArgs

class GitPushTest extends Specification {


    def "git push args"() {
        expect:
        gitPushArgs("http://g.com", ["master", "v1.0"], false) == ["git", "push", "http://g.com", "master", "v1.0"]
        gitPushArgs("http://g.com", ["master", "v1.0"], true) == ["git", "push", "http://g.com", "master", "v1.0", "--dry-run"]
    }
}
