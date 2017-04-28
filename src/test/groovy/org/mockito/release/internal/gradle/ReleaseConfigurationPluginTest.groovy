package org.mockito.release.internal.gradle

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import spock.lang.Unroll

class ReleaseConfigurationPluginTest extends Specification {

    def root = new ProjectBuilder().build()
    def subproject = new ProjectBuilder().withParent(root).build()

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

    def "should set team.addContributorsToPomFromGitHub to true by default"() {
        expect:
        root.plugins.apply(ReleaseConfigurationPlugin).configuration.team.addContributorsToPomFromGitHub == true
    }

    def "should team.addContributorsToPomFromGitHub be false when set to false"() {
        when:
        def configuration = root.plugins.apply(ReleaseConfigurationPlugin).configuration
        configuration.team.addContributorsToPomFromGitHub = false

        then:
        configuration.team.addContributorsToPomFromGitHub == false
    }

    def "knows if the release is not notable"() {
        def conf = root.plugins.apply(ReleaseConfigurationPlugin).configuration

        expect: !conf.notableRelease

        when: conf.notableRelease = true
        then: conf.notableRelease
    }

    def "knows if the release is notable"() {
        root.file("version.properties") << "version=1.5.0"

        expect:
        root.plugins.apply(ReleaseConfigurationPlugin).configuration.notableRelease
    }
}
