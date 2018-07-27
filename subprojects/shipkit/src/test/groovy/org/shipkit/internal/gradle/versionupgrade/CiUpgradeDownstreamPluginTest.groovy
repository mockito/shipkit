package org.shipkit.internal.gradle.versionupgrade

import org.shipkit.gradle.exec.ShipkitExecTask
import org.shipkit.internal.gradle.util.GradleWrapper
import testutil.PluginSpecification

class CiUpgradeDownstreamPluginTest extends PluginSpecification {

    def "should add upgradeDownstream to ciPerformRelease"() {
        when:
        project.plugins.apply(CiUpgradeDownstreamPlugin)

        then:
        ShipkitExecTask performReleaseTask = project.tasks['ciPerformRelease']
        performReleaseTask.execCommands.size() == 4
        performReleaseTask.execCommands[3].commandLine == [GradleWrapper.getWrapperCommand(), "upgradeDownstream"]
    }

}
