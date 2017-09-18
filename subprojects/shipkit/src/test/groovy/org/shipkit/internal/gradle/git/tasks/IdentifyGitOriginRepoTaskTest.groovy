package org.shipkit.internal.gradle.git.tasks

import org.gradle.testfixtures.ProjectBuilder
import testutil.PluginSpecification

class IdentifyGitOriginRepoTaskTest extends PluginSpecification {

    IdentifyGitOriginRepoTask task
    GitOriginRepoProvider originProvider = Mock(GitOriginRepoProvider)

    void setup() {
        task = project.tasks["identifyGitOrigin"]
        task.originRepoProvider = originProvider
        conf.gitHub.repository = null
    }

    def "the task is useful even if shipkit configuration plugin is not applied"() {
        def p = new ProjectBuilder().build()
        def t = p.tasks.create("identify", IdentifyGitOriginRepoTask)
        t.originRepoProvider = Stub(GitOriginRepoProvider) { getOriginGitRepo() >> "my-repo" }

        when:
        t.identifyGitOriginRepo()

        then:
        t.repository == "my-repo"
    }

    def "should use repository from shipkit configuration if provided"() {
        conf.gitHub.repository = "repo"

        when:
        task.identifyGitOriginRepo()

        then:
        task.repository == "repo"
        0 * originProvider._
    }

    def "should use fallback repo when originProvider fails"() {
        given:
        def e = new RuntimeException("test")
        originProvider.getOriginGitRepo() >> { throw e }

        when:
        task.identifyGitOriginRepo()

        then:
        task.repository == IdentifyGitOriginRepoTask.FALLBACK_GITHUB_REPO
    }

    def "should get repo from provider only once"() {
        given:
        originProvider.getOriginGitRepo() >> "originRepo"

        when:
        task.identifyGitOriginRepo()

        then:
        1 * originProvider.getOriginGitRepo() >> "originRepo"
        task.repository == "originRepo"
        conf.gitHub.repository == "originRepo"
    }
}
