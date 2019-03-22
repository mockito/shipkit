package org.shipkit.internal.gradle.release

import org.shipkit.gradle.exec.ShipkitExecTask
import org.shipkit.gradle.notes.UpdateReleaseNotesTask
import org.shipkit.internal.gradle.notes.ReleaseNotesPlugin
import testutil.PluginSpecification

class GradlePortalReleasePluginTest extends PluginSpecification {

    def "configures tasks with publication repo"() {
        conf.releaseNotes.publicationRepository = "publicRepo"

        project.plugins.apply(GradlePortalReleasePlugin)
        project.plugins.apply("com.gradle.plugin-publish")

        when:
        project.evaluate()

        then:
        UpdateReleaseNotesTask updateNotes = project.tasks.getByName(ReleaseNotesPlugin.UPDATE_NOTES_TASK)
        updateNotes.publicationRepository == "publicRepo"

        ShipkitExecTask contrib = project.tasks.getByName(ReleasePlugin.CONTRIBUTOR_TEST_RELEASE_TASK)
        contrib.execCommands*.commandLine.toString() == "[[./gradlew, releaseNeeded, performRelease, releaseCleanUp, -PdryRun, -x, gitPush, -x, updateReleaseNotesOnGitHub, -x, updateReleaseNotesOnGitHubCleanUp, -x, pushJavadoc, -x, publishPlugins]]"
    }
}
