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
        def gitHubApi = Mock(GitHubApi)

        when:
        new CreatePullRequest().createPullRequest(createPullRequestTask, gitHubApi)

        then:
        1 * gitHubApi.post("/repos/mockito/shipkit-example/pulls",
            '{  "title": "Version of shipkit upgraded to 0.1.5",' +
                '  "body": "This pull request was automatically created by Shipkit\'s' +
                            ' \'version-upgrade-customer\' Gradle plugin (http://shipkit.org).' +
                            ' Please merge it so that you are using fresh version of \'shipkit\' dependency.",' +
                '  "head": "wwilk:shipkit-version-upgraded-0.1.5",' +
                '  "base": "master"}')
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
