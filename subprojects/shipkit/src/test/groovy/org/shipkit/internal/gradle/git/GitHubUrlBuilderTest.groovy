package org.shipkit.internal.gradle.git

import org.shipkit.gradle.configuration.ShipkitConfiguration
import spock.lang.IgnoreIf
import spock.lang.Specification
import spock.lang.Unroll
import org.shipkit.gradle.configuration.ShipkitConfiguration.GitHub

class GitHubUrlBuilderTest extends Specification {

    def conf = new ShipkitConfiguration()

    @Unroll
    def "should return authorized GH url given gHurl '#ghUrl'"() {
        when :
        if (ghUrl) {
            conf.gitHub.url = ghUrl
        }

        conf.gitHub.writeAuthUser = "user"
        conf.gitHub.writeAuthToken = "token"

        then:
        GitHubUrlBuilder.getGitHubUrl("org/repo", conf) == expected

        where:
        ghUrl                     | expected
        null                      | "https://user:token@github.com/org/repo.git"
        "https://gh.ent.com:8080" | "https://user:token@gh.ent.com:8080/org/repo.git"
    }

    /**
    If GH_WRITE_TOKEN exists then the value of that is used over null inside the ShipkitConfiguration class
    see {@link org.shipkit.internal.gradle.configuration.ShipkitConfigurationStore#getValue}
     */
    @Unroll
    @IgnoreIf({System.getenv(GitHub.GH_WRITE_TOKEN)})
    def "should return unauthorized GH url given gHurl '#ghUrl'"() {
        when :
        if (ghUrl) {
            conf.gitHub.url = ghUrl
        }

        then:
        GitHubUrlBuilder.getGitHubUrl("org/repo", conf) == expected

        where:
        ghUrl                     | expected
        null                      | "https://github.com/org/repo.git"
        "https://gh.ent.com:8080" | "https://gh.ent.com:8080/org/repo.git"
    }
}
