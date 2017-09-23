package org.shipkit.internal.gradle.release.tasks

import org.shipkit.internal.util.GitHubApi
import testutil.PluginSpecification

class UploadGistsTest extends PluginSpecification {

    def "should upload gists correctly"() {
        given:
        tmp.newFile("test.log") << "content"
        tmp.newFile("test2.log") << "content2"

        def task = project.tasks.create("uploadGists", UploadGistsTask.class);
        task.rootDir = tmp.root.absolutePath
        task.filesPatterns = ["**/**.log"]

        def gitHubApi = Mock(GitHubApi)

        when:
        new UploadGists().uploadGists(task, gitHubApi)

        then:
        1 * gitHubApi.post("/gists",
            '{"public":"true",' +
            '"files":{"test.log":{"content":"content"}},' +
            '"description":"test.log"}') >> '{"html_url": "http://gist.github.com/1234"}'

        1 * gitHubApi.post("/gists",
            '{"public":"true",' +
            '"files":{"test2.log":{"content":"content2"}},' +
            '"description":"test2.log"}') >> '{"html_url": "http://gist.github.com/1234"}'
    }


}
