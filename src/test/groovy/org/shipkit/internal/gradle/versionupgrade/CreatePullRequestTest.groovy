package org.shipkit.internal.gradle.versionupgrade

import org.gradle.testfixtures.ProjectBuilder
import org.shipkit.internal.util.GitHubApi
import spock.lang.Specification

class CreatePullRequestTest extends Specification {

    def "should prepare correct url and request body"() {
        given:
        def tasksContainer = new ProjectBuilder().build().tasks
        def createPullRequestTask = tasksContainer.create("createPullRequest", CreatePullRequestTask)
        createPullRequestTask.setBaseBranch("master")
        createPullRequestTask.setHeadBranch("shipkit-version-upgraded-0.1.5")
        createPullRequestTask.setTitle("Shipkit version upgraded to 0.1.5");
        createPullRequestTask.setRepositoryUrl("mockito/shipkit-example");
        def gitHubApi = Mock(GitHubApi)

        when:
        new CreatePullRequest().createPullRequest(createPullRequestTask, gitHubApi)

        then:
        1 * gitHubApi.post("/repos/mockito/shipkit-example/pulls",
            '{  "title": "Shipkit version upgraded to 0.1.5",' +
                '  "body": "Please pull this in!",' +
                '  "head": "shipkit-version-upgraded-0.1.5",' +
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
