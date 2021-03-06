package org.shipkit.internal.gradle.plugin.tasks

import spock.lang.Specification

class PluginValidatorTest extends Specification {

    def "class candidates"(pluginId, candidates) {
        expect:
        PluginValidator.getClassCandidates(pluginId)*.toString() == candidates*.toString()

        where:
        pluginId                                | candidates
        'org.shipkit.bintray'                   | ['BintrayPlugin', 'ShipkitBintrayPlugin', 'OrgShipkitBintrayPlugin']
        'org.shipkit.github-pom-contributors'   | ['GithubPomContributorsPlugin', 'ShipkitGithubPomContributorsPlugin', 'OrgShipkitGithubPomContributorsPlugin']
        'org.shipkit.gradle-plugin'             | ['GradlePlugin', 'ShipkitGradlePlugin', 'OrgShipkitGradlePlugin']
    }
}
