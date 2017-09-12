package org.shipkit.internal.gradle.git

import org.shipkit.gradle.git.GitPushTask
import org.shipkit.gradle.git.IdentifyGitBranchTask
import testutil.PluginSpecification

class GitPluginTest extends PluginSpecification {

    def "applies"() {
        expect:
        project.plugins.apply(GitPlugin)
    }

    def "sets git push auth when identify task is executed"() {
        project.plugins.apply(GitPlugin)
        conf.gitHub.repository = 'my-repo'
        conf.gitHub.writeAuthToken = 'foo'

        when:
        project.tasks[GitAuthPlugin.IDENTIFY_GIT_ORIGIN_TASK].execute()

        then:
        GitPushTask gitPush = project.tasks[GitPlugin.GIT_PUSH_TASK]
        gitPush.url == "https://dummy:foo@github.com/my-repo.git"
        gitPush.secretValue == "foo"
    }

    def "sets git push branch when identify task is executed"() {
        project.plugins.apply(GitPlugin)
        assert project.version == '1.5.23'

        when:
        IdentifyGitBranchTask b = project.tasks[GitBranchPlugin.IDENTIFY_GIT_BRANCH]
        b.branch = 'some-branch'
        b.execute()

        then:
        GitPushTask gitPush = project.tasks[GitPlugin.GIT_PUSH_TASK]
        gitPush.targets == ['v1.5.23', 'some-branch']
    }
}
