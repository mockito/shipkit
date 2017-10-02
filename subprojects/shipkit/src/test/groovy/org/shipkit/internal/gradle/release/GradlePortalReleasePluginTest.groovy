package org.shipkit.internal.gradle.release

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
    }
}
