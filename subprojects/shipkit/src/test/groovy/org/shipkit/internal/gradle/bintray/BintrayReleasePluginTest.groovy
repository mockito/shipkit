package org.shipkit.internal.gradle.bintray

import org.shipkit.gradle.notes.UpdateReleaseNotesTask
import org.shipkit.internal.gradle.java.JavaBintrayPlugin
import org.shipkit.internal.gradle.notes.ReleaseNotesPlugin
import testutil.PluginSpecification

class BintrayReleasePluginTest extends PluginSpecification {

    def "configures tasks"() {
        project.plugins.apply(BintrayReleasePlugin)
        project.plugins.apply(JavaBintrayPlugin)

        project.bintray.pkg.userOrg = "some-org"
        project.bintray.pkg.repo = "some-repo"
        project.bintray.pkg.name = "some-pkg"

        when:
        project.evaluate()

        then:
        UpdateReleaseNotesTask updateNotes = project.tasks.getByName(ReleaseNotesPlugin.UPDATE_NOTES_TASK)
        updateNotes.publicationRepository == "https://bintray.com/some-org/some-repo/some-pkg"
    }
}
