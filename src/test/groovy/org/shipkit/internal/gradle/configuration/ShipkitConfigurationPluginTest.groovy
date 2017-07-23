package org.shipkit.internal.gradle.configuration

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.shipkit.gradle.init.InitShipkitFileTask
import testutil.PluginSpecification

class ShipkitConfigurationPluginTest extends PluginSpecification {

    Project root
    Project subproject

    void setup(){
        root = new ProjectBuilder().withProjectDir(tmp.root).build()
        subproject = new ProjectBuilder().withParent(root).build()
    }

    def "singleton configuration, root applied first"() {
        expect:
        root.plugins.apply(ShipkitConfigurationPlugin).configuration == subproject.plugins.apply(ShipkitConfigurationPlugin).configuration
    }

    def "singleton configuration, subproject applied first"() {
        expect:
        subproject.plugins.apply(ShipkitConfigurationPlugin).configuration == root.plugins.apply(ShipkitConfigurationPlugin).configuration
    }

    def "dry run off by default"() {
        expect:
        !root.plugins.apply(ShipkitConfigurationPlugin).configuration.dryRun
    }

    def "configures dry run based on project property"() {
        when:
        root.ext.'dryRun' = ''

        then:
        root.plugins.apply(ShipkitConfigurationPlugin).configuration.dryRun
    }

    def "configures initShipkitFile task correctly"() {
        when:
        root.plugins.apply(ShipkitConfigurationPlugin)

        then:
        InitShipkitFileTask task = root.tasks.findByName(ShipkitConfigurationPlugin.INIT_SHIPKIT_FILE_TASK)
        task.shipkitFile == root.file(ShipkitConfigurationPlugin.SHIPKIT_FILE_RELATIVE_PATH)
    }

    def "loads default properties if config file does not exist"() {
        given:
        assert !root.file(ShipkitConfigurationPlugin.SHIPKIT_FILE_RELATIVE_PATH).exists()

        when:
        def conf = root.plugins.apply(ShipkitConfigurationPlugin).configuration

        then:
        conf.gitHub.url == "https://github.com"
        conf.gitHub.apiUrl == "https://api.github.com"
        conf.gitHub.repository == "mockito/shipkit"
        conf.gitHub.readOnlyAuthToken == "e7fe8fcfd6ffedac384c8c4c71b2a48e646ed1ab"
    }

    @Override
    void createShipkitConfiguration(){
        // default are not needed in this test
    }

    @Override
    void createShipkitFile(){
        // config file created in setup is not needed in this test
    }
}
