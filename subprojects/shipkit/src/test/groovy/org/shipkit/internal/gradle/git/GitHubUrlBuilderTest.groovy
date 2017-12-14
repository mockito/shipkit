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

    def "should return Custom GH url whith auth"() {
        def conf = new ShipkitConfiguration()
        conf.gitHub.writeAuthToken = "token"
        conf.gitHub.writeAuthUser = "user"
        conf.gitHub.url = "https://github.enterprise.com:123456"

        expect:
        GitHubUrlBuilder.getGitHubUrl("org/repo", conf) == "https://user:token@github.enterprise.com:123456/org/repo.git"
    }

    def "should return Custom GH url whith auth, even with trailing /"() {
        def conf = new ShipkitConfiguration()
        conf.gitHub.writeAuthToken = "token"
        conf.gitHub.writeAuthUser = "user"
        conf.gitHub.url = "http://192.168.0.199/"

        expect:
        GitHubUrlBuilder.getGitHubUrl("org/repo", conf) == "http://user:token@192.168.0.199/org/repo.git"
    }

    def "should return GH url without auth by default"() {
        expect:
        GitHubUrlBuilder._getGitHubUrl(null, null, "org/repo", null) == "https://github.com/org/repo.git"
    }
}
