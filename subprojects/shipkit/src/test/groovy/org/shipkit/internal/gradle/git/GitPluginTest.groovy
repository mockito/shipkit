package org.shipkit.internal.gradle.git

import org.shipkit.gradle.git.GitCommitTask
import org.shipkit.gradle.git.GitPushTask
import spock.util.environment.RestoreSystemProperties
import testutil.PluginSpecification

class GitPluginTest extends PluginSpecification {

    def "applies"() {
        expect:
        project.plugins.apply(GitPlugin)
    }

    def "configures git push"() {
        conf.gitHub.repository = 'my-repo'
        conf.gitHub.writeAuthToken = 'foo'

        when:
        project.plugins.apply(GitPlugin)

        then:
        GitPushTask gitPush = project.tasks[GitPlugin.GIT_PUSH_TASK]
        gitPush.url == "https://dummy:foo@github.com/my-repo.git"
        gitPush.secretValue == "foo"
    }

    def "configures git commit"() {
        conf.gitHub.repository = 'my-repo'
        conf.gitHub.writeAuthToken = 'foo'

        when:
        project.plugins.apply(GitPlugin)

        then:
        GitCommitTask gitCommit = project.tasks[GitPlugin.GIT_COMMIT_TASK]
        gitCommit.commitMessagePostfix == "[ci skip]"
    }

    @RestoreSystemProperties
    def "configures git commit in Travis environment"() {
        conf.gitHub.repository = 'my-repo'
        conf.gitHub.writeAuthToken = 'foo'
        System.setProperty("TRAVIS_BUILD_NUMBER", "test")
        when:
        project.plugins.apply(GitPlugin)

        then:
        GitCommitTask gitCommit = project.tasks[GitPlugin.GIT_COMMIT_TASK]
        gitCommit.commitMessagePostfix == "CI job: https://travis-ci.org/my-repo/builds/test [ci skip]"
    }
}
