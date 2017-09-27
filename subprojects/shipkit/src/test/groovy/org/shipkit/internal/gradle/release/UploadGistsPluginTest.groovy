package org.shipkit.internal.gradle.release

import org.shipkit.internal.gradle.release.tasks.UploadGistsTask
import testutil.PluginSpecification

class UploadGistsPluginTest extends PluginSpecification {

    def "should configure uploadGists task"() {
        given:
        conf.gitHub.writeAuthToken = "writeToken"
        conf.gitHub.apiUrl = "apiUrl"

        when:
        project.plugins.apply(UploadGistsPlugin)

        then:
        UploadGistsTask task = project.tasks.findByName("uploadGists")
        task.gitHubWriteToken == "writeToken"
        task.gitHubApiUrl == "apiUrl"
    }

}
