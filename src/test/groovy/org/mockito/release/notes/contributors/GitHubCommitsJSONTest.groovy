package org.mockito.release.notes.contributors

import org.json.simple.JSONObject
import spock.lang.Specification

class GitHubCommitsJSONTest extends Specification {

    def "parse commits"() {
        def commits = new JSONObject([commit: [author: [name: "Continuous Delivery Drone"]],
                                      author: [login: "continuous-delivery-drone",
                                               html_url: "https://github.com/continuous-delivery-drone"]])

        when:
        def contributor = GitHubCommitsJSON.toContributor(commits)

        then:
        contributor.name == "Continuous Delivery Drone"
        contributor.login == "continuous-delivery-drone"
        contributor.profileUrl == "https://github.com/continuous-delivery-drone"
    }

}
