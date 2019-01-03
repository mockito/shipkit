package org.shipkit.internal.gradle.notes.tasks

import org.shipkit.gradle.notes.UpdateReleaseNotesOnGitHubCleanupTask
import org.shipkit.gradle.notes.UpdateReleaseNotesOnGitHubTask
import org.shipkit.internal.notes.header.HeaderProvider
import org.shipkit.internal.util.GitHubApi
import spock.lang.Specification

class UpdateReleaseNotesOnGitHubTest extends Specification {

    def urlReleaseIdByTagName = "/repos/mockito/shipkit-example/releases/tags/v1.0.0"
    def urlEditRelease = "/repos/mockito/shipkit-example/releases/1234"
    def body = '{"body":"text"}'
    def responseReleaseIdByTagName = "{\n" +
        "  \"id\": 1234\n" +
        "}"
    def responseEditRelease = "{\n" +
        "  \"html_url\": \"https://github.com/mockito/shipkit-example/releases/v1.0.0\"" +
        "}"

    def "should generate notes and POST release notes to GitHub"() {
        setup:
        def gitHubApi = Mock(GitHubApi)
        def updateReleaseNotes = Mock(UpdateReleaseNotes)
        def task = Mock(UpdateReleaseNotesOnGitHubTask)
        task.upstreamRepositoryName >> "mockito/shipkit-example"
        task.tagPrefix >> "v"
        task.version >> "1.0.0"

        def header = new HeaderProvider()
        def update = new UpdateReleaseNotesOnGitHub(gitHubApi, updateReleaseNotes)

        when:
        update.updateReleaseNotes(task, header)

        then:
        1 * updateReleaseNotes.generateNewContent(task, header) >> "text"
        1 * gitHubApi.get(urlReleaseIdByTagName) >> responseReleaseIdByTagName
        1 * gitHubApi.patch(urlEditRelease, body) >> responseEditRelease
    }

    def "should clean up release notes on GitHub"() {
        setup:
        def gitHubApi = Mock(GitHubApi)
        def updateReleaseNotes = Mock(UpdateReleaseNotes)
        def task = Mock(UpdateReleaseNotesOnGitHubCleanupTask)
        task.upstreamRepositoryName >> "mockito/shipkit-example"
        task.tagPrefix >> "v"
        task.version >> "1.0.0"

        def update = new UpdateReleaseNotesOnGitHub(gitHubApi, updateReleaseNotes)

        when:
        update.removeReleaseNotes(task)

        then:
        1 * gitHubApi.get(urlReleaseIdByTagName) >> responseReleaseIdByTagName
        1 * gitHubApi.delete("/repos/mockito/shipkit-example/releases/1234") >> responseReleaseIdByTagName
    }
}
