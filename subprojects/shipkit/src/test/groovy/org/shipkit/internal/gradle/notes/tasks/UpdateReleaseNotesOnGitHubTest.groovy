package org.shipkit.internal.gradle.notes.tasks

import org.shipkit.gradle.notes.UpdateReleaseNotesOnGitHubTask
import org.shipkit.internal.notes.header.HeaderProvider
import org.shipkit.internal.util.GitHubApi
import spock.lang.Specification

class UpdateReleaseNotesOnGitHubTest extends Specification {

    def url = "/repos/mockito/shipkit-example/releases"
    def body = '{"tag_name":"v1.0.0","prerelease":false,"draft":false,"name":"v1.0.0","body":"text"}'
    def response = "{\n" +
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
        1 * gitHubApi.post(url, body) >> response
    }
}
