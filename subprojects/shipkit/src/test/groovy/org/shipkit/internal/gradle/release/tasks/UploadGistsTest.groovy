package org.shipkit.internal.gradle.release.tasks

import testutil.PluginSpecification

class UploadGistsTest extends PluginSpecification {

    def "should upload gists correctly"() {
        given:
        tmp.newFile("test.log") << "content"
        tmp.newFile("test2.log") << "content2"

        def task = project.tasks.create("uploadGists", UploadGistsTask.class);
        task.filesToUpload = project.files("test.log", "test2.log")

        def gistsApi = Mock(GistsApi)

        when:
        new UploadGists().uploadGists(task, gistsApi)

        then:
        1 * gistsApi.uploadFile("test.log", "content")
        1 * gistsApi.uploadFile("test2.log", "content2")
    }

    def "should still try to upload other files if uploading one of them fails"() {
        given:
        tmp.newFile("test.log") << "content"
        tmp.newFile("test2.log") << "content2"
        tmp.newFile("test3.log") << "content3"

        def task = project.tasks.create("uploadGists", UploadGistsTask.class);
        task.filesToUpload = project.files("test.log", "test2.log", "test3.log")

        def gistsApi = Mock(GistsApi)

        when:
        new UploadGists().uploadGists(task, gistsApi)

        then:
        1 * gistsApi.uploadFile("test.log", "content") >> { throw new RuntimeException() }
        // check if other uploadFile calls were executed
        1 * gistsApi.uploadFile("test2.log", "content2")
        1 * gistsApi.uploadFile("test3.log", "content3")
        thrown(RuntimeException)
    }
}
