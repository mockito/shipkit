package org.shipkit.internal.gradle.versionupgrade

import org.gradle.api.GradleException
import org.gradle.testfixtures.ProjectBuilder
import org.shipkit.internal.util.GitHubApi
import spock.lang.Specification

class MergePullRequestTest extends Specification {

    def "should not call github API in dryRun mode"() {
        given:
        def tasksContainer = new ProjectBuilder().build().tasks
        def mergePullRequestTask = tasksContainer.create("mergePullRequest", MergePullRequestTask)
        mergePullRequestTask.setDryRun(true)
        def gitHubApi = Mock(GitHubApi)

        when:
        new MergePullRequest().mergePullRequest(mergePullRequestTask, gitHubApi)

        then:
        0 * gitHubApi._
    }

    def "should prepare correct url and request body with retrying once"() {
        given:
        def tasksContainer = new ProjectBuilder().build().tasks
        def mergePullRequestTask = tasksContainer.create("mergePullRequestTask", MergePullRequestTask)
        def versionUpgrade = new UpgradeDependencyExtension(
                baseBranch: "master", dependencyName: "shipkit", newVersion: "0.1.5")
        mergePullRequestTask.setVersionBranch("shipkit-version-upgraded-0.1.5")
        mergePullRequestTask.setUpstreamRepositoryName("mockito/shipkit-example")
        mergePullRequestTask.setForkRepositoryName("wwilk/shipkit-example")
        mergePullRequestTask.setVersionUpgrade(versionUpgrade)
        def gitHubApi = Mock(GitHubApi)

        when:
        new MergePullRequest().mergePullRequest(mergePullRequestTask, gitHubApi)

        then:
        1 * gitHubApi.get("/repos/mockito/shipkit-example/branches/wwilk:shipkit-version-upgraded-0.1.5") >> "{\"commit\":{\"sha\": \"testSha\"}}"
        1 * gitHubApi.get("/repos/mockito/shipkit-example/statuses/testSha") >> "[{\"state\": \"pending\", \"description\": \"desc\"}]"
        1 * gitHubApi.get("/repos/mockito/shipkit-example/statuses/testSha") >> "[{\"state\": \"success\", \"description\": \"desc\"}]"
        1 * gitHubApi.post("/repos/mockito/shipkit-example/merges", '{  "head": "wwilk:shipkit-version-upgraded-0.1.5",  "base": "master"}')
    }

    def "should throw exception in case of failure"() {
        given:
        def tasksContainer = new ProjectBuilder().build().tasks
        def mergePullRequestTask = tasksContainer.create("mergePullRequestTask", MergePullRequestTask)
        def versionUpgrade = new UpgradeDependencyExtension(
                baseBranch: "master", dependencyName: "shipkit", newVersion: "0.1.5")
        mergePullRequestTask.setVersionBranch("shipkit-version-upgraded-0.1.5")
        mergePullRequestTask.setUpstreamRepositoryName("mockito/shipkit-example")
        mergePullRequestTask.setForkRepositoryName("wwilk/shipkit-example")
        mergePullRequestTask.setVersionUpgrade(versionUpgrade)
        def gitHubApi = Mock(GitHubApi)

        when:
        new MergePullRequest().mergePullRequest(mergePullRequestTask, gitHubApi)

        then:
        1 * gitHubApi.get("/repos/mockito/shipkit-example/branches/wwilk:shipkit-version-upgraded-0.1.5") >> "{\"commit\":{\"sha\": \"testSha\"}}"
        1 * gitHubApi.get("/repos/mockito/shipkit-example/statuses/testSha") >> "[{\"state\": \"failure\", \"description\": \"desc\"}]"
        def e = thrown(GradleException)
        e.message == "Status of check 'desc':failure. Merge aborted"
    }

    def "should throw exception in case of error"() {
        given:
        def tasksContainer = new ProjectBuilder().build().tasks
        def mergePullRequestTask = tasksContainer.create("mergePullRequestTask", MergePullRequestTask)
        def versionUpgrade = new UpgradeDependencyExtension(
                baseBranch: "master", dependencyName: "shipkit", newVersion: "0.1.5")
        mergePullRequestTask.setVersionBranch("shipkit-version-upgraded-0.1.5")
        mergePullRequestTask.setUpstreamRepositoryName("mockito/shipkit-example")
        mergePullRequestTask.setForkRepositoryName("wwilk/shipkit-example")
        mergePullRequestTask.setVersionUpgrade(versionUpgrade)
        def gitHubApi = Mock(GitHubApi)

        when:
        new MergePullRequest().mergePullRequest(mergePullRequestTask, gitHubApi)

        then:
        1 * gitHubApi.get("/repos/mockito/shipkit-example/branches/wwilk:shipkit-version-upgraded-0.1.5") >> "{\"commit\":{\"sha\": \"testSha\"}}"
        1 * gitHubApi.get("/repos/mockito/shipkit-example/statuses/testSha") >> "[{\"state\": \"error\", \"description\": \"desc\"}]"
        def e = thrown(GradleException)
        e.message == "Status of check 'desc':error. Merge aborted"
    }
}
