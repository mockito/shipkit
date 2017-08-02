package org.shipkit.internal.gradle.git

import org.apache.commons.lang.RandomStringUtils
import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.shipkit.gradle.git.GitCommitTask
import spock.lang.Specification

import static java.io.File.separator

class GitCommitTaskTest extends Specification {

    def tasksContainer = new ProjectBuilder().build().tasks
    def gitCommitTask = tasksContainer.create("gitCommitTask", GitCommitTask)

    def "aggregated commit message is empty when no changes registered"() {
        expect:
        gitCommitTask.aggregatedCommitMessage == ""
    }

    def "list of files is empty when no changes registered"() {
        expect:
        gitCommitTask.files.isEmpty()
    }

    def "aggregates message correctly"() {
        when:
        gitCommitTask.addChange([], "release notes updated", anyTask())
        gitCommitTask.addChange([], "version bumped", anyTask())

        then:
        gitCommitTask.aggregatedCommitMessage == "release notes updated + version bumped"
    }

    def "aggregates files correctly"() {
        given:
        def basePath = new File("").absolutePath
        when:
        gitCommitTask.addChange([new File("test"), new File("test2")], "", anyTask())
        gitCommitTask.addChange([new File("test3")], "", anyTask())

        then:
        gitCommitTask.files == [basePath + separator + "test",
                                basePath + separator + "test2",
                                basePath + separator + "test3"]
    }

    Task anyTask() {
        return tasksContainer.create(RandomStringUtils.random(15), DefaultTask)
    }
}
