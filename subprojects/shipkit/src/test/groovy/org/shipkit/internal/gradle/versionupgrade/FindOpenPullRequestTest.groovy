package org.shipkit.internal.gradle.versionupgrade

import org.shipkit.internal.util.GitHubApi
import spock.lang.Specification

class FindOpenPullRequestTest extends Specification {

    FindOpenPullRequest findOpenPullRequest = new FindOpenPullRequest()

    def "should return null if response is empty"() {
        given:
        def gitHubApi = Mock(GitHubApi)
        gitHubApi.get("/repos/repo/pulls?state=open") >> "[ ]"

        expect:
        null == findOpenPullRequest.findOpenPullRequest("repo", null, gitHubApi)
    }

    def "should return null if head->ref does not match versionBranchRegex"() {
        given:
        def gitHubApi = Mock(GitHubApi)
        gitHubApi.get("/repos/repo/pulls?state=open") >> "[{\"number\": 123, \"head\" : {\"ref\" : \"shipkit-1.2\"}} ]"

        expect:
        null == findOpenPullRequest.findOpenPullRequest("repo", "shipkit-[0-9]*", gitHubApi)
    }

    def "should return head->ref if it matches versionBranchRegex"() {
        given:
        def gitHubApi = Mock(GitHubApi)
        gitHubApi.get("/repos/repo/pulls?state=open") >> "[{\"number\": 123, \"url\": \"url-1\", \"head\" : {\"ref\" : \"shipkit-1\", \"sha\" : \"sha-1\"}} ]"

        expect:
        def openPullRequest = findOpenPullRequest.findOpenPullRequest("repo", "shipkit-[0-9]*", gitHubApi)
        openPullRequest.ref == "shipkit-1"
        openPullRequest.sha == "sha-1"
        openPullRequest.url == "url-1"
        openPullRequest.number == 123
    }
}
