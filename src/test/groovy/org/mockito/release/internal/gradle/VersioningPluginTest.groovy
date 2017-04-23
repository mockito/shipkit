package org.mockito.release.internal.gradle

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.mockito.release.version.VersionInfo
import spock.lang.Specification

class VersioningPluginTest extends Specification {

    @Rule TemporaryFolder tmp = new TemporaryFolder()
    def project
    def initialVersionFileContent = "version=1.0.0\nnotableVersions=0.1.0\n"

    def setup(){
        project = new ProjectBuilder().withProjectDir(tmp.root).build()
    }

    def "should generate version properties if it doesn't exist"() {
        given:
        assert !project.file(VersioningPlugin.VERSION_FILE_NAME).exists()
        project.version("0.5.0")

        when:
        project.plugins.apply(VersioningPlugin)

        then:
        project.file(VersioningPlugin.VERSION_FILE_NAME).text ==
                "#Version of the produced binaries. This file is intended to be checked-in.\n" +
                "#It will be automatically bumped by release automation.\n" +
                "version=0.5.0\n"
    }

    def "should not modify version properties if it already exists"() {
        given:
        project.file(VersioningPlugin.VERSION_FILE_NAME) << initialVersionFileContent

        when:
        project.plugins.apply(VersioningPlugin)

        then:
        project.file(VersioningPlugin.VERSION_FILE_NAME).text == initialVersionFileContent
    }


    def "should set extensions properly"() {
        given:
        project.file(VersioningPlugin.VERSION_FILE_NAME) << initialVersionFileContent

        when:
        project.plugins.apply(VersioningPlugin)

        then:
        def versionInfo = project.extensions.getByType(VersionInfo)
        versionInfo.versionFile == project.file(VersioningPlugin.VERSION_FILE_NAME)
        versionInfo.version == "1.0.0"
        versionInfo.notableVersions == ["0.1.0"] as LinkedList

        project.extensions.extraProperties.get("release_notable") == true
    }
}
