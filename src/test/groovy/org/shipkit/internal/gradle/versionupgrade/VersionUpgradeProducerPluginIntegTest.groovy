package org.shipkit.internal.gradle.versionupgrade

import testutil.GradleSpecification

class VersionUpgradeProducerPluginIntegTest extends GradleSpecification {

    def "all tasks in dry run"() {
        projectDir.newFolder("gradle")
        projectDir.newFile("gradle/shipkit.gradle") << """
            shipkit {
                gitHub.url = "http://github.com"
            }
        """

        buildFile << """
            apply plugin: "org.shipkit.version-upgrade-producer"
            
            versionUpgradeProducer{
                consumersRepositoriesNames = ['wwilk/mockito']
            }
        """

        projectDir.newFile("version.properties") << "version=1.0.0"

        expect:
        def result = pass("produceVersionUpgrade", "-m", "-s")
        result.tasks.join("\n") == """:cloneConsumerRepoWwilkMockito=SKIPPED
:produceVersionUpgradeWwilkMockito=SKIPPED
:produceVersionUpgrade=SKIPPED"""
    }
}
