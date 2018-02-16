package org.shipkit.internal.gradle.versionupgrade

import org.gradle.api.GradleException
import org.shipkit.gradle.exec.ShipkitExecTask
import org.shipkit.gradle.git.GitPushTask
import org.shipkit.internal.gradle.configuration.LazyConfiguration
import org.shipkit.internal.gradle.git.GitOriginPlugin
import org.shipkit.internal.gradle.git.domain.PullRequest
import org.shipkit.internal.gradle.git.tasks.GitCheckOutTask
import org.shipkit.internal.gradle.git.tasks.GitPullTask
import org.shipkit.internal.gradle.git.tasks.IdentifyGitOriginRepoTask
import spock.lang.Unroll
import testutil.PluginSpecification

import static org.shipkit.internal.gradle.versionupgrade.UpgradeDependencyPlugin.CREATE_PULL_REQUEST
import static org.shipkit.internal.gradle.versionupgrade.UpgradeDependencyPlugin.PULL_UPSTREAM
import static org.shipkit.internal.gradle.versionupgrade.UpgradeDependencyPlugin.PUSH_VERSION_UPGRADE

class UpgradeDependencyPluginTest extends PluginSpecification {

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
        project.plugins.apply(UpgradeDependencyPlugin)
        ReplaceVersionTask task = project.tasks.replaceVersion
        project.evaluate()

        then:
        with(task) {
            buildFile == project.file("build.gradle")
            dependencyName == "shipkit"
            dependencyGroup == "org.shipkit"
            newVersion == "0.1.2"
        }
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
        project.plugins.apply(UpgradeDependencyPlugin)
        CreatePullRequestTask task = project.tasks.createPullRequest
        project.evaluate()

        then:
        task.gitHubApiUrl == "http://api.com"
        task.authToken == "writeToken"
        task.baseBranch == "master"
        task.pullRequestDescription == "This pull request was automatically created by " +
                "Shipkit's 'org.shipkit.upgrade-downstream' Gradle plugin (http://shipkit.org). " +
                "Please merge it so that you are using fresh version of 'shipkit' dependency."
        task.pullRequestTitle == "Version of shipkit upgraded to 1.2.30"
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
        def pr = Optional.of(new PullRequest().setRef("openPR"))

        expect:
        "openPR" == UpgradeDependencyPlugin.getCurrentVersionBranchName(null, null, pr)
    }

    def "should return new version branch if open pull request branch is null"() {
        expect:
        "upgrade-shipkit-to-1.2.3" == UpgradeDependencyPlugin.getCurrentVersionBranchName("shipkit", "1.2.3", Optional.ofNullable(null))
    }

    @Unroll
    def "should throw exception when executing specific tasks and dependency not set"() {
        when:
        project.plugins.apply(UpgradeDependencyPlugin)
        def t = project.tasks.getByName(task)
        LazyConfiguration.forceConfiguration(t)

        then:
        def ex = thrown GradleException
        ex.message == "Dependency project property not set. It is required for task '$t.path'.\n" +
            "You can pass project property via command line: -Pdependency=\"org.shipkit:shipkit:1.2.3\""

        where:
        task << ['commitVersionUpgrade', 'findOpenPullRequest', 'replaceVersion', 'pushVersionUpgrade', 'createPullRequest']
    }

    def "dependency project property is not needed during Gradle's configuration"() {
        when:
        project.plugins.apply(UpgradeDependencyPlugin)

        then:
        //safe to evaluate despite there is no 'dependency' project property
        //we only want to validate when user runs task that needs the 'dependency' project property
        project.evaluate()
    }
}
