package org.shipkit.internal.gradle.versionupgrade

import org.gradle.testkit.runner.BuildResult
import testutil.GradleSpecification

class UpgradeDownstreamPluginIntegTest extends GradleSpecification {

    def "all tasks in dry run (using gradle version #gradleVersionToTest)"() {
        given:
        gradleVersion = gradleVersionToTest

        and:
        file("gradle/shipkit.gradle") << """
            shipkit {
                gitHub.url = "http://github.com"
            }
        """

        buildFile << """
            apply plugin: "org.shipkit.upgrade-downstream"

            upgradeDownstream {
                repositories = ['wwilk/mockito']
            }
        """

        expect:
        BuildResult result = pass("upgradeDownstream", "-m", "-s")
        skippedTaskPathsGradleBugWorkaround(result.output).join("\n") == """:cloneWwilkMockito
:upgradeWwilkMockito
:upgradeDownstream"""

        where:
        gradleVersionToTest << determineGradleVersionsToTest()
    }
}
