package org.shipkit.internal.util

import org.shipkit.internal.gradle.git.domain.PullRequestStatus
import org.shipkit.internal.gradle.versionupgrade.MergePullRequestTask
import spock.lang.Specification

class GitHubStatusCheckTest extends Specification {

    MergePullRequestTask task = Mock(MergePullRequestTask)
    GitHubApi gitHubApi = Mock(GitHubApi)

    def "should return SUCCESS if status true before timeout"() {
        given:
        GitHubStatusCheck gitHubStatusCheck = new GitHubStatusCheck(task, gitHubApi, 20, 10)

        task.getPullRequestSha() >> "sha"
        task.getUpstreamRepositoryName() >> "upstreamRepo"
        1 * gitHubApi.get("/repos/upstreamRepo/commits/sha/status") >> "{\"state\": \"pending\", \"statuses\":[{\"state\":\"pending\"}]}"
        1 * gitHubApi.get("/repos/upstreamRepo/commits/sha/status") >> "{\"state\": \"success\", \"statuses\":[{\"state\":\"success\"}]}"
        when:
        def result = gitHubStatusCheck.checkStatusWithRetries()
        then:
        result == PullRequestStatus.SUCCESS
    }

    def "should throw exception if has error status"() {
        given:
        GitHubStatusCheck gitHubStatusCheck = new GitHubStatusCheck(task, gitHubApi)

        task.getPullRequestSha() >> "sha"
        task.getPullRequestUrl() >> "prURL"
        task.getUpstreamRepositoryName() >> "upstreamRepo"

        1 * gitHubApi.get("/repos/upstreamRepo/commits/sha/status") >> "{\"state\": \"error\", \"statuses\":[{\"state\":\"error\", \"description\": \"fail\", \"targetUrl\":\"tURL\"}]}"
        when:
        gitHubStatusCheck.checkStatusWithRetries()
        then:
        def e = thrown(RuntimeException)
        e.message == "Pull request prURL cannot be merged. fail. You can check details here: tURL"
    }

    def "should return NO_CHECK_DEFINED if no status defined"() {
        given:
        GitHubStatusCheck gitHubStatusCheck = new GitHubStatusCheck(task, gitHubApi, 2, 10)

        task.getPullRequestSha() >> "sha"
        task.getPullRequestUrl() >> "prURL"
        task.getUpstreamRepositoryName() >> "upstreamRepo"

        2 * gitHubApi.get("/repos/upstreamRepo/commits/sha/status") >> "{\"state\": \"pending\", \"statuses\":[]}"
        when:
        def result = gitHubStatusCheck.checkStatusWithRetries()
        then:
        result == PullRequestStatus.NO_CHECK_DEFINED
    }

    def "should return TIMEOUT if state is still pending after retries"() {
        given:
        GitHubStatusCheck gitHubStatusCheck = new GitHubStatusCheck(task, gitHubApi, 2, 10)

        task.getPullRequestSha() >> "sha"
        task.getPullRequestUrl() >> "prURL"
        task.getUpstreamRepositoryName() >> "upstreamRepo"

        2 * gitHubApi.get("/repos/upstreamRepo/commits/sha/status") >> "{\"state\": \"pending\", \"statuses\":[{\"state\":\"pending\"}]}"
        when:
        def result = gitHubStatusCheck.checkStatusWithRetries()
        then:
        result == PullRequestStatus.TIMEOUT
    }
}
