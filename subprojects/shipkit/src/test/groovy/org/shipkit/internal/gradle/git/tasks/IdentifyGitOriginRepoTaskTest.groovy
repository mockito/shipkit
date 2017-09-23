package org.shipkit.internal.gradle.git.tasks

import testutil.PluginSpecification

class IdentifyGitOriginRepoTaskTest extends PluginSpecification {

    IdentifyGitOriginRepoTask task
    GitOriginRepoProvider originProvider = Mock(GitOriginRepoProvider)

    void setup() {
        task = project.tasks["identifyGitOrigin"]
        task.originRepoProvider = originProvider
    }

    def "gets origin repo"() {
        originProvider.getOriginGitRepo() >> "my-repo"

        when:
        task.identifyGitOriginRepo()

        then:
        task.repository == "my-repo"
    }

    def "if repo is specified avoid forking off git process"() {
        task.repository = "foo"

        when:
        task.identifyGitOriginRepo()

        then:
        task.repository == "foo"
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
}
