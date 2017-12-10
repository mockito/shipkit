package org.shipkit.internal.util

import spock.lang.Specification

class GitHubApiTest extends Specification {

    def "should mask access token for post request"() {
        given:
        def api = new GitHubApi("https://api.github.com", "accessToken")

        when:
        api.post("/repos/shipkit-example/pulls", "{}")

        then:
        def ex = thrown(Exception)
        ex.message.startsWith(
            "POST https://api.github.com/repos/shipkit-example/pulls?access_token=[SECRET] failed")
    }

    def "should mask access token for get request"() {
        given:
        def api = new GitHubApi("https://api.github.com", "accessToken")

        when:
        api.get("/repos/shipkit-example/pulls")

        then:
        def ex = thrown(Exception)
        ex.message.startsWith(
            "GET https://api.github.com/repos/shipkit-example/pulls?access_token=[SECRET] failed")
    }
}
