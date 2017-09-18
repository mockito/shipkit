package org.shipkit.internal.gradle.configuration

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
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

    def "loads default properties if config file does not exist"() {
        given:
        assert !root.file(ShipkitConfigurationPlugin.SHIPKIT_FILE_RELATIVE_PATH).exists()

        when:
        def conf = root.plugins.apply(ShipkitConfigurationPlugin).configuration

        then:
        conf.gitHub.repository == "unspecified"
        conf.gitHub.readOnlyAuthToken == "unspecified"
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
