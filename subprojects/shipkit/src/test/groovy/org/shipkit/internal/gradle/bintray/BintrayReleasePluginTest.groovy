package org.shipkit.internal.gradle.bintray

import org.shipkit.gradle.exec.ShipkitExecTask
import org.shipkit.gradle.notes.UpdateReleaseNotesTask
import org.shipkit.internal.gradle.java.JavaBintrayPlugin
import org.shipkit.internal.gradle.notes.ReleaseNotesPlugin
import org.shipkit.internal.gradle.release.ReleasePlugin
import testutil.PluginSpecification

class BintrayReleasePluginTest extends PluginSpecification {

    def "configures tasks with user pulication repo"() {
        conf.releaseNotes.publicationRepository = "publicRepo"

        project.plugins.apply(BintrayReleasePlugin)
        project.plugins.apply(JavaBintrayPlugin)

        when:
        project.evaluate()

        then:
        UpdateReleaseNotesTask updateNotes = project.tasks.getByName(ReleaseNotesPlugin.UPDATE_NOTES_TASK)
        updateNotes.publicationRepository == "publicRepo"
    }

    def "configures tasks with bintray plugin pulication repo"() {
        conf.releaseNotes.publicationRepository = null

        project.plugins.apply(BintrayReleasePlugin)
        project.plugins.apply(JavaBintrayPlugin)

        project.bintray.pkg.userOrg = "some-org"
        project.bintray.pkg.repo = "some-repo"
        project.bintray.pkg.name = "some-pkg"

        when:
        project.evaluate()

        then:
        UpdateReleaseNotesTask updateNotes = project.tasks.getByName(ReleaseNotesPlugin.UPDATE_NOTES_TASK)
        updateNotes.publicationRepository == "https://bintray.com/some-org/some-repo/some-pkg/"
    }

    def "configures contributor test"() {
        when:
        project.plugins.apply(BintrayReleasePlugin)
        project.plugins.apply(ShipkitBintrayPlugin)

        then:
        ShipkitExecTask contrib = project.tasks.getByName(ReleasePlugin.CONTRIBUTOR_TEST_RELEASE_TASK)
        contrib.execCommands*.commandLine.toString() == "[[./gradlew, releaseNeeded, performRelease, releaseCleanUp, -PdryRun, -x, gitPush, -x, updateReleaseNotesOnGitHub, -x, updateReleaseNotesOnGitHubCleanUp, -x, pushJavadoc, -x, bintrayUpload]]"
    }
}
