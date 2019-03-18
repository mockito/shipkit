package org.shipkit.internal.gradle.release

import org.gradle.testfixtures.ProjectBuilder
import org.shipkit.gradle.exec.ShipkitExecTask
import spock.lang.Specification

class ReleasePluginTest extends Specification {

    def project = new ProjectBuilder().build()

    def "configures tasks"() {
        when:
        project.plugins.apply(ReleasePlugin.class)

        then:
        ShipkitExecTask contrib = project.tasks.getByName(ReleasePlugin.CONTRIBUTOR_TEST_RELEASE_TASK)
        contrib.execCommands*.commandLine.toString() == "[[./gradlew, releaseNeeded, performRelease, releaseCleanUp, -PdryRun, -x, gitPush, -x, updateReleaseNotesOnGitHub, -x, updateReleaseNotesOnGitHubCleanUp, -x, pushJavadoc]]"
    }
}
