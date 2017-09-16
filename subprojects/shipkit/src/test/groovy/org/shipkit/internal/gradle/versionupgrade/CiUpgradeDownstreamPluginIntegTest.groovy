package org.shipkit.internal.gradle.versionupgrade

import org.gradle.testkit.runner.BuildResult
import testutil.GradleSpecification

class CiUpgradeDownstreamPluginIntegTest extends GradleSpecification {

    def "all tasks in dry run"() {
        given:
        gradleVersion = gradleVersionToTest

        and:
        projectDir.newFolder("gradle")
        projectDir.newFile("gradle/shipkit.gradle") << """
            shipkit {
                gitHub.url = "http://github.com"
                gitHub.readOnlyAuthToken = "token"
                gitHub.repository = "repo"
            }
        """

        buildFile << """
            apply plugin: "org.shipkit.ci-upgrade-downstream"
            
            upgradeDownstream{
                repositories = ['wwilk/mockito']
            }
        """

        projectDir.newFile("version.properties") << "version=1.0.0"

        expect:
        BuildResult result = pass("upgradeDownstream", "-m", "-s")
        skippedTaskPathsGradleBugWorkaround(result.output).join("\n") == """:cloneWwilkMockito
:upgradeWwilkMockito
:upgradeDownstream"""

        where:
            gradleVersionToTest << determineGradleVersionsToTest()
    }
}
