package org.shipkit.internal.gradle

import org.shipkit.gradle.git.GitPushTask
import org.shipkit.internal.gradle.versionupgrade.CreatePullRequestTask
import org.shipkit.internal.gradle.configuration.LazyConfiguration
import org.shipkit.internal.util.EnvVariables
import testutil.PluginSpecification

class VersionUpgradeConsumerPluginTest extends PluginSpecification {

    def "should initialize VersionUpgradeConsumerPlugin correctly"() {
        when:
        project.plugins.apply(VersionUpgradeConsumerPlugin)

        then:
        project.tasks.versionUpgradeCheckoutBaseBranch
        project.tasks.versionUpgradeCheckoutVersionBranch
        project.tasks.versionUpgradeReplaceVersion
        project.tasks.versionUpgradeGitCommit
        project.tasks.versionUpgradeGitPush
        project.tasks.versionUpgradeCreatePullRequest
        project.tasks.performVersionUpgrade
    }

    def "should configure versionUpgradeCheckoutBaseBranch"() {
        when:
        project.extensions.baseBranch = "release/2.x"
        project.plugins.apply(VersionUpgradeConsumerPlugin)

        def task = project.tasks.versionUpgradeCheckoutBaseBranch
        LazyConfiguration.forceConfiguration(task)

        then:
        task.rev == "release/2.x"
        task.newBranch == false
    }

    def "should configure versionUpgradeCheckoutVersionBranch"() {
        when:
        project.extensions.dependencyNewVersion = "0.1.2"
        project.plugins.apply(VersionUpgradeConsumerPlugin)

        def task = project.tasks.versionUpgradeCheckoutVersionBranch
        LazyConfiguration.forceConfiguration(task)

        then:
        task.rev == "shipkit-version-bumped-0.1.2"
        task.newBranch == true
    }

    def "should configure versionUpgradeReplaceVersion"() {
        given:
        def dependencyFile = tmp.newFile("gradle.properties")
        project.extensions.dependencyNewVersion = "0.1.2"
        project.extensions.dependencyBuildFile = dependencyFile
        project.extensions.dependencyPattern = "shipkit:{VERSION}"

        when:
        project.plugins.apply(VersionUpgradeConsumerPlugin)

        LazyConfiguration.forceConfiguration(project.tasks.versionUpgradeReplaceVersion)

        then:
        def task = project.tasks.versionUpgradeReplaceVersion
        task.newVersion == "0.1.2"
        task.buildFile == dependencyFile
        task.dependencyPattern == "shipkit:{VERSION}"
    }

    def "should configure versionUpgradeReplaceVersion with default values"() {
        given:
        project.extensions.dependencyNewVersion = "0.1.2"

        when:
        project.plugins.apply(VersionUpgradeConsumerPlugin)

        LazyConfiguration.forceConfiguration(project.tasks.versionUpgradeReplaceVersion)

        then:
        def task = project.tasks.versionUpgradeReplaceVersion
        task.newVersion == "0.1.2"
        task.buildFile == project.file("build.gradle")
        task.dependencyPattern == "org.shipkit:shipkit:{VERSION}"
    }

    def "should configure versionUpgradeGitCommit"() {
        given:
        def dependencyFile = tmp.newFile("gradle.properties")
        project.extensions.dependencyNewVersion = "1.2.30"
        project.extensions.dependencyBuildFile = dependencyFile

        when:
        project.plugins.apply(VersionUpgradeConsumerPlugin)

        LazyConfiguration.forceConfiguration(project.tasks.versionUpgradeGitCommit)

        then:
        project.tasks.versionUpgradeGitCommit.commandLine ==
            ["git", "commit", "-m", "Shipkit version updated to 1.2.30", dependencyFile.absolutePath]
    }

    def "should configure versionUpgradeGitPush"() {
        given:
        project.extensions.dependencyNewVersion = "1.2.30"

        when:
        project.plugins.apply(VersionUpgradeConsumerPlugin)

        GitPushTask task = project.tasks.versionUpgradeGitPush
        LazyConfiguration.forceConfiguration(task)

        then:
        task.targets == ["shipkit-version-bumped-1.2.30"]
    }

    def "should configure versionUpgradeCreatePullRequest"() {
        given:
        project.extensions.dependencyNewVersion = "1.2.30"
        project.extensions.baseBranch = "release/2.x"
        conf.gitHub.apiUrl = "http://api.com"
        conf.gitHub.repository = "http://repository.com"

        EnvVariables envVariables = Mock(EnvVariables)
        envVariables.getenv("GH_WRITE_TOKEN") >> "token"

        when:
        new VersionUpgradeConsumerPlugin(envVariables).apply(project)
        CreatePullRequestTask task = project.tasks.versionUpgradeCreatePullRequest
        LazyConfiguration.forceConfiguration(task)

        then:
        task.gitHubApiUrl == "http://api.com"
        task.repositoryUrl == "http://repository.com"
        task.authToken == "token"
        task.title == "Shipkit version bumped to 1.2.30"
        task.baseBranch == "release/2.x"
        task.headBranch == "shipkit-version-bumped-1.2.30"
    }
}
