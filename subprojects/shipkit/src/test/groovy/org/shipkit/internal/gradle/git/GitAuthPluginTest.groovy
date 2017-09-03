package org.shipkit.internal.gradle.git

import testutil.PluginSpecification

class GitAuthPluginTest extends PluginSpecification {

    def "should set correct GitAuth extension"() {
        given:
        conf.gitHub.writeAuthToken = "token"
        conf.gitHub.writeAuthUser = "user"
        conf.gitHub.repository = "shipkit/example"

        when:
        def gitAuth = project.plugins.apply(GitAuthPlugin).gitAuth

        then:
        gitAuth.secretValue == "token"
        gitAuth.repositoryUrl == "https://user:token@github.com/shipkit/example.git"
    }

    def "should return GH url with auth"() {
        conf.gitHub.writeAuthToken = "token"
        conf.gitHub.writeAuthUser = "user"

        expect:
        GitAuthPlugin.getGitHubUrl("org/repo", conf) == "https://user:token@github.com/org/repo.git"
    }

    def "should return GH url without auth by default"() {
        expect:
        GitAuthPlugin._getGitHubUrl(null, "org/repo", null) == "https://github.com/org/repo.git"
    }
}
