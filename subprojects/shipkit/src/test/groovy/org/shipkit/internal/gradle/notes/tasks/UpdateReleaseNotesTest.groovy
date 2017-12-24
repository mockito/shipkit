package org.shipkit.internal.gradle.notes.tasks

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.shipkit.gradle.notes.UpdateReleaseNotesTask
import org.shipkit.internal.notes.contributors.DefaultProjectContributorsSet
import org.shipkit.internal.notes.model.Contributor
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

    def "check contributorsMap"() {
        when:
        def map = UpdateReleaseNotes.contributorsMap(contributorsFromConfig , new DefaultProjectContributorsSet(), developers, githubUrl)

        then:
        map.size() == expected.size()
        expected.each {
            def expectedName = it[0]
            def expectedUsername = it[1]
            def expectedUrl = it[2]
            Contributor contributor = map[expectedName]
            assert contributor.name == expectedName
            assert contributor.login == expectedUsername
            assert contributor.profileUrl == expectedUrl
        }

        where:
        contributorsFromConfig  | developers                | githubUrl                 | expected
        ['epeee:Erhard Pointl'] | ['dev:Another Developer'] | "https://www.github.com"  | [["Erhard Pointl", "epeee", "https://www.github.com/epeee"], ["Another Developer", "dev", "https://www.github.com/dev"]]
        ['epeee:Erhard Pointl'] | []                        | "https://gh.ent.com"      | [["Erhard Pointl", "epeee", "https://gh.ent.com/epeee"]]
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
