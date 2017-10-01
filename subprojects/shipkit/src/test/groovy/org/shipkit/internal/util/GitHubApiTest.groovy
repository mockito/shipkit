package org.shipkit.internal.util

import spock.lang.Specification

class GitHubApiTest extends Specification {

    def "should mask access token"() {
        given:
        def api = new GitHubApi("https://api.github.com", "accessToken")

        when:
        api.post("/repos/shipkit-example/pulls", "{}")

        then:
        def ex = thrown(Exception)
        ex.message.startsWith(
            "POST https://api.github.com/repos/shipkit-example/pulls?access_token=[SECRET] failed")
    }
}
