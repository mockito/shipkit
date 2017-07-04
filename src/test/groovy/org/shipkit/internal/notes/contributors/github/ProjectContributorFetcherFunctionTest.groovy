package org.shipkit.internal.notes.contributors.github

import org.json.simple.JsonObject
import org.shipkit.internal.notes.model.ProjectContributor
import org.shipkit.internal.notes.util.GitHubObjectFetcher
import spock.lang.Specification

class ProjectContributorFetcherFunctionTest extends Specification {

    def "Apply"() {
        given:
        def jsonObject = Mock(JsonObject)
        def authToken = "a0a4c0f41c200f7c653323014d6a72a127764e17"
        def contributorFetcherFunction = new ProjectContributorFetcherFunction(new GitHubObjectFetcher(authToken));

        jsonObject.get('url') >> "https://api.github.com/users/epeee"

        when:
        ProjectContributor contributor = contributorFetcherFunction.apply(jsonObject)

        then:
        contributor
        contributor.login == 'epeee'
        contributor.name
        contributor.name.contains('Erhard')
    }
}
