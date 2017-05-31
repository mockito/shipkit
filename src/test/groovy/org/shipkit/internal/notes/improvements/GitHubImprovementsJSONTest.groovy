package org.shipkit.internal.notes.improvements

import org.json.simple.JsonArray
import org.json.simple.JsonObject
import spock.lang.Specification

class GitHubImprovementsJSONTest extends Specification {

    def "parses issue"() {
        def issue = new JsonObject([number: new BigDecimal(100), html_url: "http://issues/100", title: "Some bugfix"])
        def labels = new JsonArray()
        labels.add(new JsonObject([name: "bugfix"]))
        labels.add(new JsonObject([name: "notable"]))
        issue.put("labels", labels)

        when:
        def i = org.shipkit.internal.notes.improvements.GitHubImprovementsJSON.toImprovement(issue)

        then:
        i.id == 100L
        i.title == "Some bugfix"
        i.url == "http://issues/100"
        i.labels.toString() == "[bugfix, notable]"
        !i.pullRequest
    }

    def "parses pull request without labels"() {
        def issue = new JsonObject([number: new BigDecimal(100), html_url: "http://issues/100", title: "Some bugfix", pull_request: [:]])
        issue.put("labels", new JsonArray())

        when:
        def i = org.shipkit.internal.notes.improvements.GitHubImprovementsJSON.toImprovement(issue)

        then:
        i.id == 100L
        i.labels.isEmpty()
        i.pullRequest
    }
}
