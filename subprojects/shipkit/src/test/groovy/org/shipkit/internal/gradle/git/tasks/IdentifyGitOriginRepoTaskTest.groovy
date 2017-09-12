package org.shipkit.internal.gradle.git.tasks

import testutil.PluginSpecification

class IdentifyGitOriginRepoTaskTest extends PluginSpecification {

    IdentifyGitOriginRepoTask task
    GitOriginRepoProvider originProvider = Mock(GitOriginRepoProvider)

    void setup() {
        task = project.tasks["identifyGitOrigin"]
        task.originRepoProvider = originProvider
        conf.gitHub.repository = null
    }

    def "should use repository from shipkit configuration if provided"() {
        conf.gitHub.repository = "repo"

        when:
        task.identifyGitOriginRepo()

        then:
        task.originRepo == "repo"
        0 * originProvider._
    }

    def "should not call originProvider when originRepo already set manually"() {
        given:
        task.originRepo = "origin"

        when:
        task.identifyGitOriginRepo()

        then:
        task.originRepo == "origin"
        0 * originProvider._
    }

    def "should use fallback repo when originProvider fails"() {
        given:
        def e = new RuntimeException("test")
        originProvider.getOriginGitRepo() >> { throw e }

        when:
        task.identifyGitOriginRepo()

        then:
        task.originRepo == IdentifyGitOriginRepoTask.FALLBACK_GITHUB_REPO
    }

    def "should get repo from provider only once"() {
        given:
        originProvider.getOriginGitRepo() >> "originRepo"

        when:
        task.identifyGitOriginRepo()
        task.identifyGitOriginRepo() //2nd call to verify single call to provider

        then:
        1 * originProvider.getOriginGitRepo() >> "originRepo"
        task.originRepo == "originRepo"
    }
}
