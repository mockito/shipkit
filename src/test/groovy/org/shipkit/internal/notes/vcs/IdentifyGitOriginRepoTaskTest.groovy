package org.shipkit.internal.notes.vcs

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class IdentifyGitOriginRepoTaskTest extends Specification {

    IdentifyGitOriginRepoTask task
    GitOriginRepoProvider originProvider

    void setup(){
        task = new ProjectBuilder().build().tasks.create("identifyGitOrigin", IdentifyGitOriginRepoTask)
        originProvider = Mock(GitOriginRepoProvider)
        task.originRepoProvider = originProvider
    }

    def "should not call originProvider when originRepo already set manually"() {
        given:
        task.originRepo = "origin"

        when:
        task.identifyGitOriginRepo()

        then:
        task.originRepo == "origin"
        task.executionException == null
        0 * originProvider.getOriginGitRepo()
    }

    def "should set executionException when originProvider fails"() {
        given:
        def exception = new RuntimeException("test")
        originProvider.getOriginGitRepo() >> { throw exception}

        when:
        task.identifyGitOriginRepo()

        then:
        task.originRepo == null
        task.executionException == exception
    }

    def "should set originRepo correctly"() {
        given:
        originProvider.getOriginGitRepo() >> "originRepo"

        when:
        task.identifyGitOriginRepo()

        then:
        task.originRepo == "originRepo"
        task.executionException == null
    }
}
