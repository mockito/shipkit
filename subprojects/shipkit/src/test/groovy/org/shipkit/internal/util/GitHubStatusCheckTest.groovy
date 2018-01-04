package org.shipkit.internal.util

import org.shipkit.internal.gradle.versionupgrade.MergePullRequestTask
import spock.lang.Specification

class GitHubStatusCheckTest extends Specification {

    MergePullRequestTask task = Mock(MergePullRequestTask)
    GitHubApi gitHubApi = Mock(GitHubApi)

    GitHubStatusCheck gitHubStatusCheck = new GitHubStatusCheck(task, gitHubApi)

    def "should return true if status true before timeout"() {
        given:
        task.getPullRequestSha() >> "sha"
        task.getUpstreamRepositoryName() >> "upstreamRepo"
        1 * gitHubApi.get("/repos/upstreamRepo/commits/sha/status") >> "{\"state\": \"pending\"}"
        1 * gitHubApi.get("/repos/upstreamRepo/commits/sha/status") >> "{\"state\": \"success\"}"
        when:
        def result = gitHubStatusCheck.checkStatusWithTimeout()
        then:
        result == true
    }

    def "should return true if has error status"() {
        given:
        task.getPullRequestSha() >> "sha"
        task.getPullRequestUrl() >> "prURL"
        task.getUpstreamRepositoryName() >> "upstreamRepo"

        1 * gitHubApi.get("/repos/upstreamRepo/commits/sha/status") >> "{\"state\": \"error\", \"statuses\":[{\"state\":\"error\", \"description\": \"fail\", \"targetUrl\":\"tURL\"}]}"
        when:
        gitHubStatusCheck.checkStatusWithTimeout()
        then:
        def e = thrown(RuntimeException)
        e.message == "Pull request prURL cannot be merged. fail. You can check details here: tURL"
    }
}
