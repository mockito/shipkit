package org.shipkit.internal.gradle.versionupgrade

import org.gradle.testfixtures.ProjectBuilder
import org.shipkit.internal.util.GitHubApi
import spock.lang.Specification

class CreatePullRequestTaskTest extends Specification {

    def "should prepare correct url and request body"() {
        given:
        def tasksContainer = new ProjectBuilder().build().tasks
        def createPullRequestTask = tasksContainer.create("createPullRequest", CreatePullRequestTask)
        createPullRequestTask.setBaseBranch("master")
        createPullRequestTask.setHeadBranch("shipkit-version-bumped-0.1.5")
        createPullRequestTask.setTitle("Shipkit version updated to 0.1.5");
        createPullRequestTask.setRepositoryUrl("mockito/shipkit-example");

        def gitHubApi = Mock(GitHubApi)
        createPullRequestTask.gitHubApi = gitHubApi

        when:
        createPullRequestTask.createPullRequest()

        then:
        1 * gitHubApi.post("/repos/mockito/shipkit-example/pulls",
            '{  "title": "Shipkit version updated to 0.1.5",' +
            '  "body": "Please pull this in!",' +
            '  "head": "shipkit-version-bumped-0.1.5",' +
            '  "base": "master"}')
    }
}
