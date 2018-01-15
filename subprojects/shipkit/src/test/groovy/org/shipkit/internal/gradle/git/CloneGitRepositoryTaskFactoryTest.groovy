package org.shipkit.internal.gradle.git

import org.gradle.testfixtures.ProjectBuilder
import testutil.PluginSpecification

import static java.io.File.separatorChar

class CloneGitRepositoryTaskFactoryTest extends PluginSpecification {

    def "returns correct consumerRepoCloneDir"() {
        def buildDir = tmp.newFolder("buildTest")
        project.setBuildDir(buildDir)

        when:
        def result = CloneGitRepositoryTaskFactory.getConsumerRepoCloneDir(project, "mockito/shipkit-example")

        then:
        result.absolutePath == buildDir.absolutePath + "${separatorChar}downstream${separatorChar}mockitoShipkitExample"
    }

    def "configures cloneRepoName task"() {
        given:
        def buildDir = tmp.newFolder("buildTest")
        project.setBuildDir(buildDir)

        when:
        def cloneTask = CloneGitRepositoryTaskFactory.createCloneTask(project, "gitHubUrl", "mockito/mockito")

        then:
        cloneTask.repositoryUrl == "gitHubUrl/mockito/mockito"
        cloneTask.targetDir.absolutePath == buildDir.absolutePath + "${separatorChar}downstream${separatorChar}mockitoMockito"
        cloneTask.name == "cloneMockitoMockito"
    }

    def "creates only one instance of cloneRepoName"() {
        when:
        def one = CloneGitRepositoryTaskFactory.createCloneTask(project, "gitHubUrl", "mockito/mockito")
        def another = CloneGitRepositoryTaskFactory.createCloneTask(project, "gitHubUrl", "mockito/mockito")

        then:
        one == another
    }

    def "creates cloneRepoName task in root project"() {
        given:
        def child = new ProjectBuilder().withParent(project).withName("child").build()

        when:
        CloneGitRepositoryTaskFactory.createCloneTask(child, "gitHubUrl", "mockito/mockito")

        then:
        child.tasks.findByName("cloneMockitoMockito") == null
        project.tasks.cloneMockitoMockito
    }
}
