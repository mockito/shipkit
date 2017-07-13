package org.shipkit.internal.gradle.configuration

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.shipkit.gradle.init.InitConfigFileTask
import testutil.PluginSpecification

class ReleaseConfigurationPluginTest extends PluginSpecification {

    Project root
    Project subproject

    void setup(){
        root = new ProjectBuilder().withProjectDir(tmp.root).build()
        subproject = new ProjectBuilder().withParent(root).build()
    }

    def "singleton configuration, root applied first"() {
        expect:
        root.plugins.apply(ReleaseConfigurationPlugin).configuration == subproject.plugins.apply(ReleaseConfigurationPlugin).configuration
    }

    def "singleton configuration, subproject applied first"() {
        expect:
        subproject.plugins.apply(ReleaseConfigurationPlugin).configuration == root.plugins.apply(ReleaseConfigurationPlugin).configuration
    }

    def "dry run off by default"() {
        expect:
        !root.plugins.apply(ReleaseConfigurationPlugin).configuration.dryRun
    }

    def "configures dry run based on project property"() {
        when:
        root.ext.'dryRun' = ''

        then:
        root.plugins.apply(ReleaseConfigurationPlugin).configuration.dryRun
    }

    def "configures initConfigFile task correctly"() {
        when:
        root.plugins.apply(ReleaseConfigurationPlugin)

        then:
        InitConfigFileTask initConfigTask = root.tasks.findByName(ReleaseConfigurationPlugin.INIT_CONFIG_FILE_TASK)
        initConfigTask.configFile == root.file(ReleaseConfigurationPlugin.CONFIG_FILE_RELATIVE_PATH)
    }

    def "loads default properties if config file does not exist"() {
        given:
        assert !root.file(ReleaseConfigurationPlugin.CONFIG_FILE_RELATIVE_PATH).exists()

        when:
        def conf = root.plugins.apply(ReleaseConfigurationPlugin).configuration

        then:
        conf.gitHub.url == "https://github.com"
        conf.gitHub.apiUrl == "https://api.github.com"
        conf.gitHub.repository == "mockito/shipkit"
        conf.gitHub.readOnlyAuthToken == "e7fe8fcfd6ffedac384c8c4c71b2a48e646ed1ab"
    }

    @Override
    void createReleaseConfiguration(){
        // default are not needed in this test
    }

    @Override
    void createConfigFile(){
        // config file created in setup is not needed in this test
    }
}
