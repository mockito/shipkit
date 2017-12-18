package org.shipkit.internal.gradle.git

import org.shipkit.gradle.configuration.ShipkitConfiguration
import spock.lang.Specification
import spock.lang.Unroll

class GitHubUrlBuilderTest extends Specification {

    public static final String DEFAULT = "USE_DEFAULT"
    def conf = new ShipkitConfiguration()

    @Unroll
    def "should return GH url given gHurl '#ghUrl' repo '#repo' user '#user' token '#token'"() {
        when :
        if (ghUrl.equalsIgnoreCase(DEFAULT) == false) {
            conf.gitHub.url = ghUrl;
        }
        if (user.equalsIgnoreCase(DEFAULT) == false) {
            conf.gitHub.writeAuthUser = user
        }
        if (token.equalsIgnoreCase(DEFAULT) == false) {
            conf.gitHub.writeAuthToken = token
        }

        then:
        GitHubUrlBuilder.getGitHubUrl("org/repo", conf) == expected

        where:
        repo        | token   | user    | ghUrl                     | expected
        "org/repo"  | DEFAULT | DEFAULT | DEFAULT                   | "https://github.com/org/repo.git"
        "org/repo"  | "token" | "user"  | DEFAULT                   | "https://user:token@github.com/org/repo.git"
        "org/repo"  | DEFAULT | DEFAULT | "https://gh.ent.com:8080" | "https://gh.ent.com:8080/org/repo.git"
        "org/repo"  | "token" | "user"  | "https://gh.ent.com:8080" | "https://user:token@gh.ent.com:8080/org/repo.git"
    }
}
