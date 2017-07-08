package org.shipkit.internal.gradle

import org.shipkit.internal.gradle.configuration.LazyConfiguration
import testutil.PluginSpecification

class AutoUpdateConsumerPluginTest extends PluginSpecification {

    def "should initialize AutoUpdateConsumerPlugin correctly"() {
        when:
        project.plugins.apply(AutoUpdateConsumerPlugin)

        then:
        project.tasks.autoUpdateCheckoutBranch
        project.tasks.autoUpdateReplaceVersion
        project.tasks.autoUpdateGitCommit
        project.tasks.autoUpdateGitPush
        project.tasks.performAutoUpdate
    }

    def "should configure autoUpdateCheckoutBranch"() {
        when:
        project.extensions.shipkitNewVersion = "0.1.2"
        project.plugins.apply(AutoUpdateConsumerPlugin)

        LazyConfiguration.forceConfiguration(project.tasks.autoUpdateCheckoutBranch)

        then:
        project.tasks.autoUpdateCheckoutBranch.commandLine ==
            ["git", "checkout", "-b", "shipkit-bumped-version-0.1.2"]
    }

    def "should configure autoUpdateReplaceVersion"() {
        given:
        def dependencyFile = tmp.newFile("gradle.properties")
        project.extensions.shipkitNewVersion = "0.1.2"
        project.extensions.shipkitDependencyFile = dependencyFile
        project.extensions.shipkitDependencyPattern = "shipkit:{VERSION}"

        when:
        project.plugins.apply(AutoUpdateConsumerPlugin)

        LazyConfiguration.forceConfiguration(project.tasks.autoUpdateReplaceVersion)

        then:
        def task = project.tasks.autoUpdateReplaceVersion
        task.newVersion == "0.1.2"
        task.configFile == dependencyFile
        task.dependencyPattern == "shipkit:{VERSION}"
    }

    def "should configure autoUpdateReplaceVersion with default values"() {
        given:
        project.extensions.shipkitNewVersion = "0.1.2"

        when:
        project.plugins.apply(AutoUpdateConsumerPlugin)

        LazyConfiguration.forceConfiguration(project.tasks.autoUpdateReplaceVersion)

        then:
        def task = project.tasks.autoUpdateReplaceVersion
        task.newVersion == "0.1.2"
        task.configFile == project.file("build.gradle")
        task.dependencyPattern == "org.shipkit:shipkit:{VERSION}"
    }

    def "should configure autoUpdateGitCommit"() {
        given:
        def dependencyFile = tmp.newFile("gradle.properties")
        project.extensions.shipkitNewVersion = "1.2.30"
        project.extensions.shipkitDependencyFile = dependencyFile

        when:
        project.plugins.apply(AutoUpdateConsumerPlugin)

        LazyConfiguration.forceConfiguration(project.tasks.autoUpdateGitCommit)

        then:
        project.tasks.autoUpdateGitCommit.commandLine ==
            ["git", "commit", "-m", "Shipkit version updated to 1.2.30", dependencyFile.absolutePath]
    }

    def "should configure autoUpdateGitPush"() {
        given:
        project.extensions.shipkitNewVersion = "1.2.30"

        when:
        project.plugins.apply(AutoUpdateConsumerPlugin)

        LazyConfiguration.forceConfiguration(project.tasks.autoUpdateGitPush)

        then:
        project.tasks.autoUpdateGitPush.commandLine ==
            ["git", "push", "-u", "origin", "shipkit-bumped-version-1.2.30"]
    }

    @Override
    void createReleaseConfiguration() {
        // ReleaseConfiguration is not needed in this test
    }
}
