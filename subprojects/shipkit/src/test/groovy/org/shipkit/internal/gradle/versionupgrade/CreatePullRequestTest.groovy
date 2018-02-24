package org.shipkit.internal.gradle.versionupgrade

import org.gradle.testfixtures.ProjectBuilder
import org.shipkit.internal.util.GitHubApi
import spock.lang.Specification

class CreatePullRequestTest extends Specification {

    def "should prepare correct url and request body"() {
        given:
        def tasksContainer = new ProjectBuilder().build().tasks
        def createPullRequestTask = tasksContainer.create("createPullRequest", CreatePullRequestTask)
        createPullRequestTask.setVersionBranch("shipkit-version-upgraded-0.1.5")
        createPullRequestTask.setUpstreamRepositoryName("mockito/shipkit-example")
        createPullRequestTask.setForkRepositoryName("wwilk/shipkit-example")
        createPullRequestTask.setBaseBranch("master")
        createPullRequestTask.setPullRequestDescription("Description of this PR")
        createPullRequestTask.setPullRequestTitle("Title of this PR")
        def gitHubApi = Mock(GitHubApi)

        when:
        def result = new CreatePullRequest().createPullRequest(createPullRequestTask, gitHubApi)

        then:
        result.ref == "shipkit-1"
        result.url == "url-1"
        result.sha == "sha-1"
        1 * gitHubApi.post("/repos/mockito/shipkit-example/pulls",
            '{  "title": "Title of this PR",' +
                '  "body": "Description of this PR",' +
                '  "head": "wwilk:shipkit-version-upgraded-0.1.5",' +
                '  "base": "master",' +
                '  "maintainer_can_modify": true}') >> "{\"number\": 123, \"url\": \"url-1\", \"head\" : {\"ref\" : \"shipkit-1\", \"sha\" : \"sha-1\"}}"
    }

    def "should not call github API in dryRun mode"() {
        given:
        def tasksContainer = new ProjectBuilder().build().tasks
        def createPullRequestTask = tasksContainer.create("createPullRequest", CreatePullRequestTask)
        createPullRequestTask.setDryRun(true)
        def gitHubApi = Mock(GitHubApi)

        when:
        new CreatePullRequest().createPullRequest(createPullRequestTask, gitHubApi)

        then:
        0 * gitHubApi._
    }
}
