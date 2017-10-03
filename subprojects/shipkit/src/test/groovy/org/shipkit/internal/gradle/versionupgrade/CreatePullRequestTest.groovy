package org.shipkit.internal.gradle.versionupgrade

import org.gradle.testfixtures.ProjectBuilder
import org.shipkit.internal.util.GitHubApi
import spock.lang.Specification

class CreatePullRequestTest extends Specification {

    def "should prepare correct url and request body"() {
        given:
        def tasksContainer = new ProjectBuilder().build().tasks
        def createPullRequestTask = tasksContainer.create("createPullRequest", CreatePullRequestTask)
        def versionUpgrade = new UpgradeDependencyExtension(
            baseBranch: "master", dependencyName: "shipkit", newVersion: "0.1.5")
        createPullRequestTask.setVersionBranch("shipkit-version-upgraded-0.1.5")
        createPullRequestTask.setUpstreamRepositoryName("mockito/shipkit-example")
        createPullRequestTask.setForkRepositoryName("wwilk/shipkit-example")
        createPullRequestTask.setVersionUpgrade(versionUpgrade)
        createPullRequestTask.setPullRequestDescription("Description of this PR")
        createPullRequestTask.setPullRequestTitle("Title of this PR")
        def gitHubApi = Mock(GitHubApi)

        when:
        new CreatePullRequest().createPullRequest(createPullRequestTask, gitHubApi)

        then:
        1 * gitHubApi.post("/repos/mockito/shipkit-example/pulls",
            '{  "title": "Title of this PR",' +
                '  "body": "Description of this PR",' +
                '  "head": "wwilk:shipkit-version-upgraded-0.1.5",' +
                '  "base": "master",' +
                '  "maintainer_can_modify": true}')
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

    def "should throw exception when pull request title is empty"() {
        given:
        def tasksContainer = new ProjectBuilder().build().tasks
        def createPullRequestTask = tasksContainer.create("createPullRequest", CreatePullRequestTask)
        createPullRequestTask.setPullRequestDescription("Description of PR")
        def gitHubApi = Mock(GitHubApi)

        when:
        new CreatePullRequest().createPullRequest(createPullRequestTask, gitHubApi)

        then:
        def e = thrown(IllegalArgumentException)
        e.getMessage() == "Cannot create pull request for empty pull request title. Set it with git.pullRequestTitle property in configuration."
    }

    def "should not call github API when pull request description is empty"() {
        given:
        def tasksContainer = new ProjectBuilder().build().tasks
        def createPullRequestTask = tasksContainer.create("createPullRequest", CreatePullRequestTask)
        createPullRequestTask.setPullRequestTitle("Title of PR")
        def gitHubApi = Mock(GitHubApi)

        when:
        new CreatePullRequest().createPullRequest(createPullRequestTask, gitHubApi)

        then:
        def e = thrown(IllegalArgumentException)
        e.getMessage() == "Cannot create pull request for empty pull request description. Set it with git.pullRequestDescription property in configuration."
    }
}
