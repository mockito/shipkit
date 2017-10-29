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

    def "returns correct consumerRepoCloneDir"() {
        def buildDir = tmp.newFolder("buildTest")
        project.setBuildDir(buildDir)

        when:
        def result = CloneGitRepositoryTask.getConsumerRepoCloneDir(project, "mockito/shipkit-example")

        then:
        result.absolutePath == buildDir.absolutePath + "/downstream/mockitoShipkitExample"
    }

    def "configures cloneRepoName task"() {
        given:
        def buildDir = tmp.newFolder("buildTest")
        project.setBuildDir(buildDir)

        when:
        def cloneTask = CloneGitRepositoryTask.createCloneTask(project, "gitHubUrl", "mockito/mockito")

        then:
        cloneTask.repositoryUrl == "gitHubUrl/mockito/mockito"
        cloneTask.targetDir.absolutePath == buildDir.absolutePath + "/downstream/mockitoMockito"
        cloneTask.name == "cloneMockitoMockito"
    }

    def "creates only one instance of cloneRepoName"() {
        when:
        def one = CloneGitRepositoryTask.createCloneTask(project, "gitHubUrl", "mockito/mockito")
        def another = CloneGitRepositoryTask.createCloneTask(project, "gitHubUrl", "mockito/mockito")

        then:
        one == another
    }

    def "creates cloneRepoName task in root project"() {
        given:
        def child = new ProjectBuilder().withParent(project).withName("child").build()

        when:
        CloneGitRepositoryTask.createCloneTask(child, "gitHubUrl", "mockito/mockito")

        then:
        child.tasks.findByName("cloneMockitoMockito") == null
        project.tasks.cloneMockitoMockito
    }
}
