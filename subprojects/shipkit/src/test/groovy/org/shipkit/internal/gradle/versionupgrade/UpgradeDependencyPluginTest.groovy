package org.shipkit.internal.gradle.versionupgrade

import org.gradle.api.GradleException
import org.gradle.api.Task
import org.shipkit.gradle.exec.ShipkitExecTask
import org.shipkit.gradle.git.GitPushTask
import org.shipkit.internal.gradle.configuration.LazyConfiguration
import org.shipkit.internal.gradle.git.GitOriginPlugin
import org.shipkit.internal.gradle.git.tasks.GitCheckOutTask
import org.shipkit.internal.gradle.git.tasks.GitPullTask
import org.shipkit.internal.gradle.git.tasks.IdentifyGitOriginRepoTask
import testutil.PluginSpecification

import static org.shipkit.internal.gradle.versionupgrade.UpgradeDependencyPlugin.CREATE_PULL_REQUEST;
import static org.shipkit.internal.gradle.versionupgrade.UpgradeDependencyPlugin.PUSH_VERSION_UPGRADE;
import static org.shipkit.internal.gradle.versionupgrade.UpgradeDependencyPlugin.PULL_UPSTREAM;

class UpgradeDependencyPluginTest extends PluginSpecification {

    public static final String DEPENDENCY_NOT_SET_EXCEPTION_MESSAGE = "Dependency property not set. You need to add 'dependency' " +
        "parameter in order to run this task. E.g.: -Pdependency=\"org.shipkit:shipkit:1.2.3\""

    def setup() {
        conf.gitHub.writeAuthToken = "secret"
    }

    def "should initialize plugin correctly and with default values"() {
        when:
        def versionUpgrade = project.plugins.apply(UpgradeDependencyPlugin).upgradeDependencyExtension

        then:
        versionUpgrade.buildFile == project.file("build.gradle")
        versionUpgrade.baseBranch == "master"

        project.tasks.checkoutBaseBranch
        project.tasks.checkoutVersionBranch
        project.tasks.replaceVersion
        project.tasks.commitVersionUpgrade
        project.tasks.pushVersionUpgrade
        project.tasks.createPullRequest
        project.tasks.mergePullRequest
        project.tasks.performVersionUpgrade
    }

    def "should configure VersionUpgrade extension basing on dependencyNewVersion parameter"() {
        given:
        project.extensions.dependency = "org.shipkit:shipkit:0.1.2"

        when:
        def versionUpgrade = project.plugins.apply(UpgradeDependencyPlugin).upgradeDependencyExtension

        then:
        versionUpgrade.dependencyGroup == "org.shipkit"
        versionUpgrade.dependencyName == "shipkit"
        versionUpgrade.newVersion == "0.1.2"
    }

    def "should configure checkoutBaseBranch"() {
        when:
        def versionUpgrade = project.plugins.apply(UpgradeDependencyPlugin).upgradeDependencyExtension
        versionUpgrade.baseBranch = "release/2.x"

        project.evaluate()
        GitCheckOutTask task = project.tasks.checkoutBaseBranch

        then:
        task.rev == "release/2.x"
        task.newBranch == false
    }

    def "should configure pullUpstream"() {
        given:
        conf.gitHub.url = "http://github.com"
        conf.gitHub.repository = "mockito/shipkit"
        conf.dryRun = true
        conf.gitHub.writeAuthToken = "writeToken"
        conf.gitHub.writeAuthUser = "writeUser"

        when:
        def versionUpgrade = project.plugins.apply(UpgradeDependencyPlugin).upgradeDependencyExtension
        versionUpgrade.baseBranch = "release/2.x"

        project.evaluate()
        GitPullTask task = project.tasks.pullUpstream

        then:
        //task.url == "https://writeUser:writeToken@github.com/mockito/shipkit.git"
        task.rev == "release/2.x"
        task.dryRun
    }

    def "configures tasks based on identified git repo"() {
        conf.gitHub.repository = 'my-org/my-repo'
        conf.gitHub.writeAuthToken = 'foo'

        when:
        project.plugins.apply(UpgradeDependencyPlugin)
        IdentifyGitOriginRepoTask t = project.tasks[GitOriginPlugin.IDENTIFY_GIT_ORIGIN_TASK]
        t.repository = 'some-user/my-repo'
        t.execute()

        then:
        GitPullTask pull = project.tasks[PULL_UPSTREAM]
        pull.secretValue == 'foo'
        pull.url == 'https://dummy:foo@github.com/my-org/my-repo.git'

        GitPushTask push = project.tasks[PUSH_VERSION_UPGRADE]
        push.secretValue == 'foo'
        push.url == 'https://dummy:foo@github.com/my-org/my-repo.git'

        CreatePullRequestTask pr = project.tasks[CREATE_PULL_REQUEST]
        pr.forkRepositoryName == 'some-user/my-repo'
        pr.upstreamRepositoryName == 'my-org/my-repo'
    }

    def "should configure replaceVersion"() {
        given:
        project.extensions.dependency = "org.shipkit:shipkit:0.1.2"

        when:
        UpgradeDependencyExtension versionUpgrade = project.plugins.apply(UpgradeDependencyPlugin).upgradeDependencyExtension
        ReplaceVersionTask task = project.tasks.replaceVersion

        then:
        task.versionUpgrade == versionUpgrade
    }

    def "should configure gitCommitVersionUpgrade"() {
        given:
        def dependencyFile = tmp.newFile("gradle.properties")
        project.extensions.dependency = "org.shipkit:shipkit:1.2.30"

        when:
        def versionUpgrade = project.plugins.apply(UpgradeDependencyPlugin).upgradeDependencyExtension
        versionUpgrade.buildFile = dependencyFile
        project.evaluate()

        ShipkitExecTask task = project.tasks.commitVersionUpgrade

        then:
        task.execCommands[0].commandLine ==
            ["git", "commit", "--author", "shipkit-org <<shipkit.org@gmail.com>>", "-m", "shipkit version upgraded to 1.2.30", dependencyFile.absolutePath]
    }

    def "should configure createPullRequest"() {
        given:
        project.extensions.dependency = "org.shipkit:shipkit:1.2.30"
        conf.gitHub.apiUrl = "http://api.com"
        conf.gitHub.repository = "mockito/mockito"
        conf.gitHub.writeAuthToken = "writeToken"

        when:
        def versionUpgrade = project.plugins.apply(UpgradeDependencyPlugin).upgradeDependencyExtension
        CreatePullRequestTask task = project.tasks.createPullRequest

        then:
        task.gitHubApiUrl == "http://api.com"
        task.authToken == "writeToken"
        task.baseBranch == "master"
        task.pullRequestDescription == "This pull request was automatically created by " +
            "Shipkit's 'org.shipkit.upgrade-downstream' Gradle plugin (http://shipkit.org). " +
            "Please merge it so that you are using fresh version of 'shipkit' dependency."
        task.pullRequestTitle == "Version of shipkit upgraded to ${versionUpgrade.newVersion}"
    }

    def "should configure mergePullRequest"() {
        given:
        project.extensions.dependency = "org.shipkit:shipkit:1.2.30"
        conf.gitHub.apiUrl = "http://api.com"
        conf.gitHub.repository = "mockito/mockito"
        conf.gitHub.writeAuthToken = "writeToken"

        when:
        project.plugins.apply(UpgradeDependencyPlugin).upgradeDependencyExtension
        MergePullRequestTask task = project.tasks.mergePullRequest

        then:
        task.gitHubApiUrl == "http://api.com"
        task.authToken == "writeToken"
        task.baseBranch == "master"
    }

    def "should return open pull request branch if it is not null"() {
        expect:
        "openPR" == UpgradeDependencyPlugin.getCurrentVersionBranchName(null, null, "openPR")
    }

    def "should return new version branch if open pull request branch is null"() {
        expect:
        "upgrade-shipkit-to-1.2.3" == UpgradeDependencyPlugin.getCurrentVersionBranchName("shipkit", "1.2.3", null)
    }

    def "should throw exception when executing commitVersionUpgrade and dependency not set"() {
        when:
        project.plugins.apply(UpgradeDependencyPlugin)
        Task task = project.tasks.commitVersionUpgrade
        LazyConfiguration.forceConfiguration(task)

        then:
        def ex = thrown GradleException
        ex.message == DEPENDENCY_NOT_SET_EXCEPTION_MESSAGE
    }

    def "should throw exception when executing findOpenPullRequest and dependency not set"() {
        when:
        project.plugins.apply(UpgradeDependencyPlugin)
        Task task = project.tasks.findOpenPullRequest
        LazyConfiguration.forceConfiguration(task)

        then:
        def ex = thrown GradleException
        ex.message == DEPENDENCY_NOT_SET_EXCEPTION_MESSAGE
    }

    def "should throw exception when executing replaceVersion and dependency not set"() {
        when:
        project.plugins.apply(UpgradeDependencyPlugin)
        Task task = project.tasks.replaceVersion
        LazyConfiguration.forceConfiguration(task)

        then:
        def ex = thrown GradleException
        ex.message == DEPENDENCY_NOT_SET_EXCEPTION_MESSAGE
    }

    def "should throw exception when executing pushVersionUpgrade and dependency not set"() {
        when:
        project.plugins.apply(UpgradeDependencyPlugin)
        Task task = project.tasks.pushVersionUpgrade
        LazyConfiguration.forceConfiguration(task)

        then:
        def ex = thrown GradleException
        ex.message == DEPENDENCY_NOT_SET_EXCEPTION_MESSAGE
    }

    def "should throw exception when executing createPullRequest and dependency not set"() {
        when:
        project.plugins.apply(UpgradeDependencyPlugin)
        Task task = project.tasks.createPullRequest
        LazyConfiguration.forceConfiguration(task)

        then:
        def ex = thrown GradleException
        ex.message == DEPENDENCY_NOT_SET_EXCEPTION_MESSAGE
    }

    def "should not register commitVersionUpgrade task in lazy configuration when dependency set"() {
        given:
        project.extensions.dependency = "org.shipkit:shipkit:1.2.30"

        when:
        project.plugins.apply(UpgradeDependencyPlugin)
        Task task = project.tasks.commitVersionUpgrade
        LazyConfiguration.forceConfiguration(task)

        then:
        thrown NullPointerException
    }

    def "should not register findOpenPullRequest task in lazy configuration when dependency set"() {
        given:
        project.extensions.dependency = "org.shipkit:shipkit:1.2.30"

        when:
        project.plugins.apply(UpgradeDependencyPlugin)
        Task task = project.tasks.findOpenPullRequest
        LazyConfiguration.forceConfiguration(task)

        then:
        thrown NullPointerException
    }

    def "should not register replaceVersion task in lazy configuration when dependency set"() {
        given:
        project.extensions.dependency = "org.shipkit:shipkit:1.2.30"

        when:
        project.plugins.apply(UpgradeDependencyPlugin)
        Task task = project.tasks.replaceVersion
        LazyConfiguration.forceConfiguration(task)

        then:
        thrown NullPointerException
    }

    def "should not register pushVersionUpgrade task in lazy configuration when dependency set"() {
        given:
        project.extensions.dependency = "org.shipkit:shipkit:1.2.30"

        when:
        project.plugins.apply(UpgradeDependencyPlugin)
        Task task = project.tasks.pushVersionUpgrade
        LazyConfiguration.forceConfiguration(task)

        then:
        thrown NullPointerException
    }

    def "should not register createPullRequest task in lazy configuration when dependency set"() {
        given:
        project.extensions.dependency = "org.shipkit:shipkit:1.2.30"

        when:
        project.plugins.apply(UpgradeDependencyPlugin)
        Task task = project.tasks.createPullRequest
        LazyConfiguration.forceConfiguration(task)

        then:
        thrown NullPointerException
    }
}
