package org.mockito.release.internal.gradle

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.mockito.release.version.VersionInfo
import spock.lang.Specification

class VersioningPluginTest extends Specification {

    @Rule TemporaryFolder tmp = new TemporaryFolder()
    def project

    def setup(){
        project = new ProjectBuilder().withProjectDir(tmp.root).build()
    }

    def "should generate version properties and use project.version if file doesn't exist and project.version specified"() {
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

    def "should generate version properties and use 0.0.1 version if file doesn't exist and project.version unspecified"() {
        given:
        assert !project.file(VersioningPlugin.VERSION_FILE_NAME).exists()
        assert project.version == "unspecified"

        when:
        project.plugins.apply(VersioningPlugin)

        then:
        project.file(VersioningPlugin.VERSION_FILE_NAME).text ==
                "#Version of the produced binaries. This file is intended to be checked-in.\n" +
                "#It will be automatically bumped by release automation.\n" +
                "version=0.0.1\n"
    }

    def "should not modify version properties if it already exists"() {
        given:
        project.file(VersioningPlugin.VERSION_FILE_NAME) << "version=1.0.0\nnotableVersions=0.1.0\n"

        when:
        project.plugins.apply(VersioningPlugin)

        then:
        project.file(VersioningPlugin.VERSION_FILE_NAME).text == "version=1.0.0\nnotableVersions=0.1.0\n"
    }


    def "should set extensions properly"() {
        given:
        project.file(VersioningPlugin.VERSION_FILE_NAME) << "version=1.0.0\nnotableVersions=0.1.0\n"
        when:
        project.plugins.apply(VersioningPlugin)

        then:
        def versionInfo = project.extensions.getByType(VersionInfo)
        versionInfo.versionFile == project.file(VersioningPlugin.VERSION_FILE_NAME)
        versionInfo.version == "1.0.0"
        versionInfo.notableVersions == ["0.1.0"] as LinkedList
        versionInfo.notableRelease
    }
}
