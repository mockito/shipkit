package org.shipkit.internal.notes.contributors.github

import org.json.simple.JsonObject
import spock.lang.Specification

class GitHubAllContributorsJsonTest extends Specification {

    def "parse regular contributors"() {
        def contributorJson = new JsonObject(login: "szczepiq",
                url: "https://api.github.com/users/szczepiq",
                html_url: "https://github.com/szczepiq",
                "contributions": 2427)
        def userJson = new JsonObject(login: "szczepiq",
                url: "https://api.github.com/users/szczepiq",
                name: "Szczepan Faber")

        when:
        def contributor = GitHubAllContributorsJson.toContributor(contributorJson, userJson)

        then:
        contributor.name == "Szczepan Faber"
        contributor.login == "szczepiq"
        contributor.profileUrl == "https://github.com/szczepiq"
        contributor.numberOfContributions == 2427
    }

    def "parse regular contributors with empty name"() {
        def contributorJson = new JsonObject(login: "continuous-delivery-drone",
                url: "https://api.github.com/users/continuous-delivery-drone",
                html_url: "https://github.com/continuous-delivery-drone",
                "contributions": 431)
        def userJson = new JsonObject(login: "continuous-delivery-drone",
                url: "https://api.github.com/users/continuous-delivery-drone",
                name: null)

        when:
        def contributor = GitHubAllContributorsJson.toContributor(contributorJson, userJson)

        then:
        contributor.name == ""
        contributor.login == "continuous-delivery-drone"
        contributor.profileUrl == "https://github.com/continuous-delivery-drone"
        contributor.numberOfContributions == 431
    }
}
