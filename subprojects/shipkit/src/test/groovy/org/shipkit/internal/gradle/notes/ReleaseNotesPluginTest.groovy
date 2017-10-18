package org.shipkit.internal.gradle.notes

import org.gradle.api.Task
import org.shipkit.gradle.git.GitCommitTask
import org.shipkit.gradle.notes.UpdateReleaseNotesTask
import org.shipkit.internal.gradle.contributors.github.GitHubContributorsPlugin
import org.shipkit.internal.gradle.git.GitPlugin
import testutil.PluginSpecification

class ReleaseNotesPluginTest extends PluginSpecification {

    def "applies cleanly"() {
        expect:
        project.plugins.apply("org.shipkit.release-notes")
    }

    def "adds updates release notes to GitCommitTask if GitPlugin applied"() {
        given:
        project.plugins.apply(GitPlugin)

        when:
        project.plugins.apply("org.shipkit.release-notes")

        then:
        GitCommitTask gitCommitTask = project.tasks.getByName(GitPlugin.GIT_COMMIT_TASK)
        gitCommitTask.filesToCommit.contains(project.file("docs/release-notes.md"))
        gitCommitTask.descriptions.contains("release notes updated")
    }

    def "should set contributorsDataFile to null if 'shipkit.team.contributors' property is not empty"() {
        given:
        def contributors = ['wwilk:Wojtek Wilk']
        conf.team.contributors = contributors

        when:
        project.plugins.apply(ReleaseNotesPlugin)

        then:
        UpdateReleaseNotesTask updateReleaseNotesTask = project.tasks.getByName(ReleaseNotesPlugin.UPDATE_NOTES_TASK)
        updateReleaseNotesTask.contributors == contributors
        updateReleaseNotesTask.contributorsDataFile == null
    }

    def "should set contributorsDataFile if 'shipkit.team.contributors' property is empty"() {
        given:
        conf.team.contributors = []

        when:
        project.plugins.apply(ReleaseNotesPlugin)

        then:
        Task contributorsTask = project.tasks.findByName(GitHubContributorsPlugin.FETCH_CONTRIBUTORS)
        UpdateReleaseNotesTask updateReleaseNotesTask = project.tasks.getByName(ReleaseNotesPlugin.UPDATE_NOTES_TASK)
        updateReleaseNotesTask.contributors == []
        updateReleaseNotesTask.contributorsDataFile == contributorsTask.outputs.files.singleFile
    }
}
