package org.shipkit.internal.gradle.notes.tasks

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.shipkit.gradle.notes.UpdateReleaseNotesTask
import spock.lang.Specification

class UpdateReleaseNotesTest extends Specification {

    @Rule
    TemporaryFolder tmp = new TemporaryFolder()

    UpdateReleaseNotes update = new UpdateReleaseNotes()

    def "should update release notes if not in preview mode"() {
        def f = tmp.newFile("release-notes.md")

        when:
        update.updateReleaseNotes(false, f, "content")

        then:
        f.text == "content"
    }

    def "should not modify releaseNotesFile if in preview mode"() {
        def f = tmp.newFile("release-notes.md")

        when:
        update.updateReleaseNotes(true, f, "content")

        then:
        f.text.isEmpty()
    }

    def "check release notes url"(branch, expectedUrl) {
        given:
        def task = Mock(UpdateReleaseNotesTask)
        def project = new ProjectBuilder().withName("myProject").withProjectDir(tmp.root).build()

        when:
        task.gitHubUrl >> "https://github.com"
        task.gitHubRepository >> 'mockito/mockito'
        task.project >> project
        task.releaseNotesFile >> project.file("doc/release-notes/official.md")

        then:
        expectedUrl == update.getReleaseNotesUrl(task, branch)

        where:
        branch          | expectedUrl
        'master'        | 'https://github.com/mockito/mockito/blob/master/doc/release-notes/official.md'
        'release/2.x'   | 'https://github.com/mockito/mockito/blob/release/2.x/doc/release-notes/official.md'
    }
}
