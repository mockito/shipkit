package org.shipkit.internal.gradle.release

import org.shipkit.internal.gradle.release.tasks.UploadGistsTask
import testutil.PluginSpecification

class UploadGistsPluginTest extends PluginSpecification {

    def "should configure uploadGists task"() {
        given:
        project.extensions[UploadGistsPlugin.FILES_PATTERNS_PROPERTY] =  "dir/*.log"

        conf.gitHub.writeAuthToken = "writeToken"
        conf.gitHub.apiUrl = "apiUrl"

        when:
        project.plugins.apply(UploadGistsPlugin).filesPatterns = ["dir/*.log"]

        then:
        UploadGistsTask task = project.tasks.findByName("uploadGists")
        task.filesPatterns == ["dir/*.log"]
        task.rootDir == project.rootDir.absolutePath
        task.gitHubWriteToken == "writeToken"
        task.gitHubApiUrl == "apiUrl"
    }

    def "should format logFilesPatterns for multiple patterns"() {
        given:
        project.extensions[UploadGistsPlugin.FILES_PATTERNS_PROPERTY] = "*.log,dir/*.txt"

        expect:
        project.plugins.apply(UploadGistsPlugin).filesPatterns == ["*.log", "dir/*.txt"]
    }

    def "should format logFilesPatterns for a single pattern"() {
        given:
        project.extensions[UploadGistsPlugin.FILES_PATTERNS_PROPERTY] =  "dir/*.log"

        expect:
        project.plugins.apply(UploadGistsPlugin).filesPatterns == ["dir/*.log"]
    }


}
