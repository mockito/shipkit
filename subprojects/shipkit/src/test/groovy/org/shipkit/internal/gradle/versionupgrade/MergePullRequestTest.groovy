package org.shipkit.internal.gradle.versionupgrade

import org.gradle.api.GradleException
import org.gradle.testfixtures.ProjectBuilder
import org.shipkit.internal.gradle.git.domain.PullRequestStatus
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
        mergePullRequestTask.setVersionBranch("shipkit-version-upgraded-0.1.5")
        mergePullRequestTask.setUpstreamRepositoryName("mockito/shipkit-example")
        mergePullRequestTask.setForkRepositoryName("wwilk/shipkit-example")
        mergePullRequestTask.setBaseBranch("master")
        mergePullRequestTask.setPullRequestSha('testSha')
        mergePullRequestTask.setPullRequestNumber(123)

        def gitHubApi = Mock(GitHubApi)
        def githubStatusCheck = Mock(GitHubStatusCheck)

        when:
        new MergePullRequest().mergePullRequest(mergePullRequestTask, gitHubApi, githubStatusCheck)

        then:
        1 * githubStatusCheck.checkStatusWithRetries() >> PullRequestStatus.SUCCESS
        1 * gitHubApi.put("/repos/mockito/shipkit-example/pulls/123/merge", '{  "merge_method": "merge",  "base": "master"}')
    }

    def "should return in case of no status checks defined"() {
        given:
        def tasksContainer = new ProjectBuilder().build().tasks
        def mergePullRequestTask = tasksContainer.create("mergePullRequestTask", MergePullRequestTask)
        mergePullRequestTask.setVersionBranch("shipkit-version-upgraded-0.1.5")
        mergePullRequestTask.setUpstreamRepositoryName("mockito/shipkit-example")
        mergePullRequestTask.setForkRepositoryName("wwilk/shipkit-example")
        mergePullRequestTask.setBaseBranch("master")
        mergePullRequestTask.setPullRequestSha('testSha')
        mergePullRequestTask.setPullRequestUrl('url-1')

        def gitHubApi = Mock(GitHubApi)
        def githubStatusCheck = Mock(GitHubStatusCheck)

        when:
        new MergePullRequest().mergePullRequest(mergePullRequestTask, gitHubApi, githubStatusCheck)

        then:
        1 * githubStatusCheck.checkStatusWithRetries() >> PullRequestStatus.NO_CHECK_DEFINED
        noExceptionThrown()
    }

    def "should throw exception in case of timeout"() {
        given:
        def tasksContainer = new ProjectBuilder().build().tasks
        def mergePullRequestTask = tasksContainer.create("mergePullRequestTask", MergePullRequestTask)
        mergePullRequestTask.setVersionBranch("shipkit-version-upgraded-0.1.5")
        mergePullRequestTask.setUpstreamRepositoryName("mockito/shipkit-example")
        mergePullRequestTask.setForkRepositoryName("wwilk/shipkit-example")
        mergePullRequestTask.setBaseBranch("master")
        mergePullRequestTask.setPullRequestSha('testSha')
        mergePullRequestTask.setPullRequestUrl('url-1')

        def gitHubApi = Mock(GitHubApi)
        def githubStatusCheck = Mock(GitHubStatusCheck)

        when:
        new MergePullRequest().mergePullRequest(mergePullRequestTask, gitHubApi, githubStatusCheck)

        then:
        1 * githubStatusCheck.checkStatusWithRetries() >> PullRequestStatus.TIMEOUT
        def e = thrown(GradleException)
        e.message == "Exception happen while trying to merge pull request. Merge aborted. Original issue: Too many retries while trying to merge url-1. Merge aborted"
    }

    def "should throw proper exception in case of error"() {
        given:
        def tasksContainer = new ProjectBuilder().build().tasks
        def mergePullRequestTask = tasksContainer.create("mergePullRequestTask", MergePullRequestTask)
        mergePullRequestTask.setVersionBranch("shipkit-version-upgraded-0.1.5")
        mergePullRequestTask.setUpstreamRepositoryName("mockito/shipkit-example")
        mergePullRequestTask.setForkRepositoryName("wwilk/shipkit-example")
        mergePullRequestTask.setBaseBranch("master")
        mergePullRequestTask.setPullRequestSha('testSha')
        mergePullRequestTask.setPullRequestUrl('url-1')
        def gitHubApi = Mock(GitHubApi)
        def githubStatusCheck = Mock(GitHubStatusCheck)

        when:
        new MergePullRequest().mergePullRequest(mergePullRequestTask, gitHubApi, githubStatusCheck)

        then:
        1 * githubStatusCheck.checkStatusWithRetries() >> { throw new RuntimeException("Error") }

        def e = thrown(GradleException)
        e.message == "Exception happen while trying to merge pull request. Merge aborted. Original issue: Error"
    }
}
