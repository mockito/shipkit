package org.mockito.release.internal.gradle

import org.gradle.api.GradleException
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Unroll
import testutil.PluginSpecification

class ReleaseConfigurationPluginTest extends PluginSpecification {

    def root
    def subproject

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

    def "dry run on by default"() {
        expect:
        root.plugins.apply(ReleaseConfigurationPlugin).configuration.dryRun
    }

    @Unroll
    def "configures dry run to #setting when project property is #property"() {
        when:
        root.ext.'releasing.dryRun' = property

        then:
        root.plugins.apply(ReleaseConfigurationPlugin).configuration.dryRun == setting

        where:
        property | setting
        "false"  | false
        "true"   | true
        ""       | true
        null     | true
    }

    def "creates shitpkit.gradle file if it doesn't exist"() {
        given:
        def configFile = new File(tmp.root.absolutePath + "/" + ReleaseConfigurationPlugin.CONFIG_FILE_RELATIVE_PATH)
        configFile.delete()

        when:
        root.plugins.apply(ReleaseConfigurationPlugin)

        then:
        thrown(GradleException)
        configFile.text ==
"""//This file was created automatically and is intented to be checked-in.
releasing {
   gitHub.repository = \"mockito/mockito-release-tools-example\"
   gitHub.readOnlyAuthToken = \"e7fe8fcfd6ffedac384c8c4c71b2a48e646ed1ab\"
   gitHub.writeAuthUser = \"shipkit\"
}

allprojects {
   plugins.withId(\"org.mockito.mockito-release-tools.bintray\") {
       bintray {
           pkg {
               repo = 'examples'
               user = 'szczepiq'
               userOrg = 'shipkit'
               name = 'basic'
               licenses = ['MIT']
               labels = ['continuous delivery', 'release automation', 'mockito']
           }
       }
   }
}
"""
    }
}
