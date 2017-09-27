package org.shipkit.internal.gradle.release.tasks

import org.shipkit.internal.util.GitHubApi
import spock.lang.Specification

class GistsApiTest extends Specification {

    def "should call GitHubApi and return correct url"(){
        given:
        def gitHubApi = Mock(GitHubApi)
        def gistsApi = new GistsApi(gitHubApi)

        when:
        def result = gistsApi.uploadFile("test.log", "content")

        then:
        result == "http://gist.github.com/1234"
        1 * gitHubApi.post("/gists",
            '{"public":"true",' +
                '"files":{"test.log":{"content":"content"}},' +
                '"description":"test.log"}') >> '{"html_url": "http://gist.github.com/1234"}'
    }

}
