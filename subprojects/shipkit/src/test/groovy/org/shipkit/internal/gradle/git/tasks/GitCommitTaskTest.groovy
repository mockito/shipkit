package org.shipkit.internal.gradle.git.tasks

import org.gradle.testfixtures.ProjectBuilder
import org.shipkit.gradle.git.GitCommitTask
import spock.lang.Specification

class GitCommitTaskTest extends Specification {

    def project = new ProjectBuilder().build()

    def "enables adding changes to commit"() {
        def f = new File("foo")
        def task = project.tasks.create("task")
        def gitCommit = project.tasks.create("foo", GitCommitTask)

        when:
        gitCommit.addChange([f], "description", task)

        then:
        gitCommit.descriptions == ["description"]
        gitCommit.filesToCommit == [f]
    }
}
