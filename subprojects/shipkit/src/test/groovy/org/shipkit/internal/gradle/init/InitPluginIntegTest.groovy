package org.shipkit.internal.gradle.init

import org.gradle.testkit.runner.BuildResult
import testutil.GradleSpecification

class InitPluginIntegTest extends GradleSpecification {

        def "runs initShipkit task in a project without any Shipkit configuration"() {
            given:
            projectDir.newFolder("gradle")

            buildFile << """
            apply plugin: "org.shipkit.java"
        """

            expect:
            BuildResult result = pass("initShipkit", "-s")

            result.tasks.collect { it.path }.join("\n") == """:identifyGitOrigin
:initShipkitFile
:initTravis
:initVersioning
:initShipkit"""

            // check if configuration files were generated
            new File(projectDir.root, "gradle/shipkit.gradle").exists()
            new File(projectDir.root, "version.properties").exists()
            new File(projectDir.root, ".travis.yml").exists()
        }
}
