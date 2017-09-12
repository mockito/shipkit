package org.shipkit.internal.gradle.git

import org.shipkit.gradle.configuration.ShipkitConfiguration
import spock.lang.Specification

class GitHubUrlBuilderTest extends Specification {

    def "should return GH url with auth"() {
        def conf = new ShipkitConfiguration()
        conf.gitHub.writeAuthToken = "token"
        conf.gitHub.writeAuthUser = "user"

        expect:
        GitHubUrlBuilder.getGitHubUrl("org/repo", conf) == "https://user:token@github.com/org/repo.git"
    }

    def "should return GH url without auth by default"() {
        expect:
        GitHubUrlBuilder._getGitHubUrl(null, "org/repo", null) == "https://github.com/org/repo.git"
    }
}
