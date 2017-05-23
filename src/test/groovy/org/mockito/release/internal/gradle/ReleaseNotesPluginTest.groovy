package org.mockito.release.internal.gradle

import testutil.PluginSpecification

class ReleaseNotesPluginTest extends PluginSpecification {

    def "applies cleanly"() {
        expect:
        project.plugins.apply("org.mockito.release-notes")
    }

    def "adds updates release notes to GitCommitTask if GitPlugin applied"() {
        given:
        project.plugins.apply(GitPlugin)

        when:
        project.plugins.apply("org.mockito.release-notes")

        then:
        GitCommitTask gitCommitTask = project.tasks.getByName(GitPlugin.GIT_COMMIT_TASK)
        gitCommitTask.files.contains(project.file("docs/release-notes.md").absolutePath)
        gitCommitTask.aggregatedCommitMessage.contains("release notes updated")
    }

}
