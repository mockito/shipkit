package org.shipkit.internal.gradle.versionupgrade

import org.gradle.api.GradleException
import org.gradle.testfixtures.ProjectBuilder
import org.shipkit.internal.util.GitHubApi
import org.shipkit.internal.util.GitHubStatusCheck
import spock.lang.Specification

class MergePullRequestTest extends Specification {

    def "should not call github API in dryRun mode"() {
        given:
        def tasksContainer = new ProjectBuilder().build().tasks
        def mergePullRequestTask = tasksContainer.create("mergePullRequest", MergePullRequestTask)
        mergePullRequestTask.setDryRun(true)
        def gitHubApi = Mock(GitHubApi)
        def githubStatusCheck = Mock(GitHubStatusCheck)

        when:
        new MergePullRequest().mergePullRequest(mergePullRequestTask, gitHubApi, githubStatusCheck)

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
        mergePullRequestTask.setPullRequestSha('testSha')

        def gitHubApi = Mock(GitHubApi)
        def githubStatusCheck = Mock(GitHubStatusCheck)

        when:
        new MergePullRequest().mergePullRequest(mergePullRequestTask, gitHubApi, githubStatusCheck)

        then:
        1 * githubStatusCheck.checkStatusWithRetries() >> true
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
        mergePullRequestTask.setPullRequestSha('testSha')
        mergePullRequestTask.setPullRequestUrl('url-1')

        def gitHubApi = Mock(GitHubApi)
        def githubStatusCheck = Mock(GitHubStatusCheck)

        when:
        new MergePullRequest().mergePullRequest(mergePullRequestTask, gitHubApi, githubStatusCheck)

        then:
        1 * githubStatusCheck.checkStatusWithRetries() >> false

        def e = thrown(GradleException)
        e.message == "Exception happen while trying to merge pull request. Merge aborted. Original issue: Too many retries while trying to merge url-1. Merge aborted"
    }

    def "should throw proper exception in case of error"() {
        given:
        def tasksContainer = new ProjectBuilder().build().tasks
        def mergePullRequestTask = tasksContainer.create("mergePullRequestTask", MergePullRequestTask)
        def versionUpgrade = new UpgradeDependencyExtension(
                baseBranch: "master", dependencyName: "shipkit", newVersion: "0.1.5")
        mergePullRequestTask.setVersionBranch("shipkit-version-upgraded-0.1.5")
        mergePullRequestTask.setUpstreamRepositoryName("mockito/shipkit-example")
        mergePullRequestTask.setForkRepositoryName("wwilk/shipkit-example")
        mergePullRequestTask.setVersionUpgrade(versionUpgrade)
        mergePullRequestTask.setPullRequestSha('testSha')
        mergePullRequestTask.setPullRequestUrl('url-1')
        def gitHubApi = Mock(GitHubApi)
        def githubStatusCheck = Mock(GitHubStatusCheck)

        when:
        new MergePullRequest().mergePullRequest(mergePullRequestTask, gitHubApi, githubStatusCheck)

        then:
        1 * githubStatusCheck.checkStatusWithRetries()

        def e = thrown(GradleException)
        e.message == "Exception happen while trying to merge pull request. Merge aborted. Original issue: Too many retries while trying to merge url-1. Merge aborted"
    }
}
