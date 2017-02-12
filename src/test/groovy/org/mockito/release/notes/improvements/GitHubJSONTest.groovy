package org.mockito.release.notes.improvements

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import spock.lang.Specification

class GitHubJSONTest extends Specification {

    def "parses issue"() {
        def issue = new JSONObject([number: 100L, html_url: "http://issues/100", title: "Some bugfix"])
        def labels = new JSONArray()
        labels.add(new JSONObject([name: "bugfix"]))
        labels.add(new JSONObject([name: "notable"]))
        issue.put("labels", labels)

        when:
        def i = GitHubJSON.toImprovement(issue)

        then:
        i.id == 100L
        i.title == "Some bugfix"
        i.url == "http://issues/100"
        i.labels.toString() == "[bugfix, notable]"
    }

    def "parses issue without labels"() {
        def issue = new JSONObject([number: 100L, html_url: "http://issues/100", title: "Some bugfix"])
        issue.put("labels", new JSONArray())

        when:
        def i = GitHubJSON.toImprovement(issue)

        then:
        i.id == 100L
        i.labels.isEmpty()
    }
}
