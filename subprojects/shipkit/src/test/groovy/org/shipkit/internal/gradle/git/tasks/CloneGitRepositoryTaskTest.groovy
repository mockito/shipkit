package org.shipkit.internal.gradle.git.tasks

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Subject
import testutil.PluginSpecification

class CloneGitRepositoryTaskTest extends PluginSpecification {

    def tempFolder

    @Subject task = new ProjectBuilder().build().tasks.create("cloneGitRepositoryTask", CloneGitRepositoryTask)

    void setup() {
        tempFolder = tmp.newFolder()
    }

    def "clone a full repository"() {
        task.repositoryUrl = "url"
        task.targetDir = tempFolder

        expect:
        task.getCloneCommand() == ["git", "clone", "url", tempFolder.getAbsolutePath()]
    }

    def "clone a shallow repository"() {
        task.repositoryUrl = "url"
        task.targetDir = tempFolder
        task.depth = 50

        expect:
        task.getCloneCommand() == ["git", "clone", "--depth", "50", "url", tempFolder.getAbsolutePath()]
    }
}
