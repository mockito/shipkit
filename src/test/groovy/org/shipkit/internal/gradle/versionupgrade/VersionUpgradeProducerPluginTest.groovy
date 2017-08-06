package org.shipkit.internal.gradle.versionupgrade

import org.gradle.api.ProjectConfigurationException
import org.gradle.api.tasks.Exec
import org.gradle.testfixtures.ProjectBuilder
import org.shipkit.internal.gradle.git.CloneGitRepositoryTask
import testutil.PluginSpecification

class VersionUpgradeProducerPluginTest extends PluginSpecification {

    def "should fail when no consumer repositories defined"() {
        when:
        project.plugins.apply(VersionUpgradeProducerPlugin).versionUpgrade
        project.evaluate()

        then:
        def ex = thrown(ProjectConfigurationException)
        ex.cause instanceof IllegalArgumentException
        ex.cause.message == "'versionUpgradeProducer.consumersRepositoriesName' cannot be null."
    }

    def "should correctly configure tasks for consumer repositories"() {
        when:
        def versionUpgrade = project.plugins.apply(VersionUpgradeProducerPlugin).versionUpgrade
        versionUpgrade.consumersRepositoriesNames = ['wwilk/shipkit-example', 'wwilk/mockito']
        project.evaluate()

        then:
        project.tasks.produceVersionUpgrade
        project.tasks['produceVersionUpgrade_wwilk_shipkit-example']
        project.tasks['produceVersionUpgrade_wwilk_mockito']
        project.tasks['cloneConsumerRepo_wwilk_shipkit-example']
        project.tasks['cloneConsumerRepo_wwilk_mockito']
    }

    def "should correctly configure clone consumer repo task"() {
        when:
        def versionUpgrade = project.plugins.apply(VersionUpgradeProducerPlugin).versionUpgrade
        versionUpgrade.consumersRepositoriesNames = ['wwilk/mockito']
        conf.gitHub.url = 'http://git.com'
        project.evaluate()

        then:
        CloneGitRepositoryTask task = project.tasks['cloneConsumerRepo_wwilk_mockito']
        task.targetDir == project.file(project.buildDir.absolutePath + '/_wwilk_mockito')
        task.repositoryUrl == 'http://git.com/wwilk/mockito'
    }

    def "should correctly configure produce version upgrade task"() {
        when:
        def versionUpgrade = project.plugins.apply(VersionUpgradeProducerPlugin).versionUpgrade
        versionUpgrade.consumersRepositoriesNames = ['wwilk/mockito']
        project.group = "depGroup"

        project.evaluate()

        then:
        Exec task = project.tasks['produceVersionUpgrade_wwilk_mockito']
        task.workingDir == project.file(project.buildDir.absolutePath + '/_wwilk_mockito')
        task.commandLine == ["./gradlew", "performVersionUpgrade", "-Pdependency=depGroup:depName:0.1.2"]
    }

    @Override
    void initProject() {
        project = new ProjectBuilder().withName("depName").withProjectDir(tmp.root).build()
        project.file("version.properties" ) << "version=0.1.3\npreviousVersion=0.1.2"
    }
}
