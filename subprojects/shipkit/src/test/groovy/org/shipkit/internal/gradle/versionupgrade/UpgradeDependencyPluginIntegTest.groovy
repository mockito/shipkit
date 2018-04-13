package org.shipkit.internal.gradle.versionupgrade

import org.gradle.testkit.runner.BuildResult
import testutil.GradleSpecification

class UpgradeDependencyPluginIntegTest extends GradleSpecification {

    def "all tasks in dry run (using gradle version #gradleVersionToTest)"() {
        given:
        gradleVersion = gradleVersionToTest

        and:
        file("gradle/shipkit.gradle") << """
            shipkit {
                gitHub.writeAuthToken = "secret"
                gitHub.repository = "repo"
            }
        """

        buildFile << """
            apply plugin: "org.shipkit.upgrade-dependency"
        """

        expect:
        BuildResult result = pass("performVersionUpgrade", "-Pdependency=org.shipkit:shipkit:0.2.3", "-m", "-s")
        skippedTaskPathsGradleBugWorkaround(result.output).join("\n") == """:checkoutBaseBranch
:identifyGitOrigin
:pullUpstream
:findOpenPullRequest
:checkoutVersionBranch
:replaceVersion
:setGitUserEmail
:setGitUserName
:commitVersionUpgrade
:pushVersionUpgrade
:createPullRequest
:mergePullRequest
:performVersionUpgrade"""

        where:
        gradleVersionToTest << determineGradleVersionsToTest()
    }
}
