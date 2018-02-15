package org.shipkit.internal.gradle.git

import org.shipkit.gradle.configuration.ShipkitConfigurationFactory
import org.shipkit.internal.util.EnvVariables
import spock.lang.Specification
import spock.lang.Unroll

class GitHubUrlBuilderTest extends Specification {

    def envVariablesMock = Mock(EnvVariables) {
        getNonEmptyEnv("GH_WRITE_TOKEN") >> null
    }
    def conf = ShipkitConfigurationFactory.create(envVariablesMock)

    @Unroll
    def "should return GH url given gHurl '#ghUrl' repo '#repo' user '#user' token '#token'"() {
        when:
        if (ghUrl) {
            conf.gitHub.url = ghUrl
        }
        if (user) {
            conf.gitHub.writeAuthUser = user
        }
        if (token) {
            conf.gitHub.writeAuthToken = token
        }

        then:
        GitHubUrlBuilder.getGitHubUrl("org/repo", conf) == expected

        where:
        repo        | token   | user    | ghUrl                     | expected
        "org/repo"  | null    | null    | null                      | "https://github.com/org/repo.git"
        "org/repo"  | "token" | "user"  | null                      | "https://user:token@github.com/org/repo.git"
        "org/repo"  | null    | null    | "https://gh.ent.com:8080" | "https://gh.ent.com:8080/org/repo.git"
        "org/repo"  | "token" | "user"  | "https://gh.ent.com:8080" | "https://user:token@gh.ent.com:8080/org/repo.git"
    }
}
