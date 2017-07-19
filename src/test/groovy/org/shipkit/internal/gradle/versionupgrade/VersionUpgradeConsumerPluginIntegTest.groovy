package org.shipkit.internal.gradle.versionupgrade

import testutil.GradleSpecification

class VersionUpgradeConsumerPluginIntegTest extends GradleSpecification {

    def "all tasks in dry run"() {
        projectDir.newFolder("gradle")
        projectDir.newFile("gradle/shipkit.gradle") << """
            shipkit {
                gitHub.writeAuthToken = "secret"
                gitHub.repository = "repo"
            }
        """

        buildFile << """
            apply plugin: "org.shipkit.version-upgrade-consumer"
        """

        projectDir.newFile("version.properties") << "version=1.0.0"

        expect:
        def result = pass("performVersionUpgrade", "-Pdependency=org.shipkit:shipkit:0.2.3", "-m", "-s")
        result.tasks.join("\n") == """:checkoutBaseBranch=SKIPPED
:pullUpstream=SKIPPED
:checkoutVersionBranch=SKIPPED
:replaceVersion=SKIPPED
:commitVersionUpgrade=SKIPPED
:pushVersionUpgrade=SKIPPED
:createPullRequest=SKIPPED
:performVersionUpgrade=SKIPPED"""
    }
}
