package org.shipkit.internal.gradle.git

import org.shipkit.gradle.git.GitPushTask
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
}
