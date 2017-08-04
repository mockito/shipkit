package org.shipkit.internal.gradle.versionupgrade

import org.gradle.api.tasks.Exec
import org.shipkit.gradle.git.GitPushTask
import org.shipkit.internal.gradle.git.GitCheckOutTask
import testutil.PluginSpecification

class VersionUpgradeConsumerPluginTest extends PluginSpecification {

    def "should initialize VersionUpgradeConsumerPlugin correctly and with default values"() {
        when:
        def versionUpgrade = project.plugins.apply(VersionUpgradeConsumerPlugin).versionUpgrade

        then:
        versionUpgrade.buildFile == project.file("build.gradle")
        versionUpgrade.baseBranch == "master"

        project.tasks.checkoutBaseBranch
        project.tasks.checkoutVersionBranch
        project.tasks.replaceVersion
        project.tasks.commitVersionUpgrade
        project.tasks.pushVersionUpgrade
        project.tasks.createPullRequest
        project.tasks.performVersionUpgrade
    }

    def "should configure VersionUpgrade extension basing on dependencyNewVersion parameter"() {
        given:
        project.extensions.dependency = "org.shipkit:shipkit:0.1.2"

        when:
        def versionUpgrade = project.plugins.apply(VersionUpgradeConsumerPlugin).versionUpgrade

        then:
        versionUpgrade.dependencyGroup == "org.shipkit"
        versionUpgrade.dependencyName == "shipkit"
        versionUpgrade.newVersion == "0.1.2"
    }

    def "should configure checkoutBaseBranch"() {
        when:
        def versionUpgrade = project.plugins.apply(VersionUpgradeConsumerPlugin).versionUpgrade
        versionUpgrade.baseBranch = "release/2.x"

        project.evaluate()
        def task = project.tasks.checkoutBaseBranch

        then:
        task.rev == "release/2.x"
        task.newBranch == false
    }

    def "should configure checkoutVersionBranch"() {
        when:
        project.extensions.dependency = "org.shipkit:shipkit:0.1.2"
        project.plugins.apply(VersionUpgradeConsumerPlugin)

        GitCheckOutTask task = project.tasks.checkoutVersionBranch

        then:
        task.rev == "upgrade-shipkit-to-0.1.2"
        task.newBranch == true
    }

    def "should configure replaceVersion"() {
        given:
        project.extensions.dependency = "org.shipkit:shipkit:0.1.2"

        when:
        VersionUpgradeConsumerExtension versionUpgrade = project.plugins.apply(VersionUpgradeConsumerPlugin).versionUpgrade
        ReplaceVersionTask task = project.tasks.replaceVersion

        then:
        task.versionUpgrade == versionUpgrade
    }

    def "should configure gitCommitVersionUpgrade"() {
        given:
        def dependencyFile = tmp.newFile("gradle.properties")
        project.extensions.dependency = "org.shipkit:shipkit:1.2.30"

        when:
        def versionUpgrade = project.plugins.apply(VersionUpgradeConsumerPlugin).versionUpgrade
        versionUpgrade.buildFile = dependencyFile
        project.evaluate()

        Exec task = project.tasks.commitVersionUpgrade

        then:
        task.commandLine ==
            ["git", "commit", "-m", "shipkit version upgraded to 1.2.30", dependencyFile.absolutePath]
    }

    def "should configure pushVersionUpgrade"() {
        given:
        project.extensions.dependency = "org.shipkit:shipkit:1.2.30"

        when:
        project.plugins.apply(VersionUpgradeConsumerPlugin)

        GitPushTask task = project.tasks.pushVersionUpgrade

        then:
        task.targets == ["upgrade-shipkit-to-1.2.30"]
    }

    def "should configure createPullRequest"() {
        given:
        project.extensions.dependency = "org.shipkit:shipkit:1.2.30"
        conf.gitHub.apiUrl = "http://api.com"
        conf.gitHub.repository = "http://repository.com"
        conf.gitHub.writeAuthToken = "writeToken"

        when:
        def versionUpgrade = project.plugins.apply(VersionUpgradeConsumerPlugin).versionUpgrade
        CreatePullRequestTask task = project.tasks.createPullRequest

        then:
        task.gitHubApiUrl == "http://api.com"
        task.repositoryUrl == "http://repository.com"
        task.authToken == "writeToken"
        task.versionUpgrade == versionUpgrade
        task.headBranch == "upgrade-shipkit-to-1.2.30"
    }
}
