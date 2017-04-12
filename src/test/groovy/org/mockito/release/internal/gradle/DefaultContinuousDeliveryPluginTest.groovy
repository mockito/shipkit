package org.mockito.release.internal.gradle

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class DefaultContinuousDeliveryPluginTest extends Specification {

    def project = new ProjectBuilder().build()

    def "applies"() {
        //TODO, without this file, the plugin breaks in ugly way
        project.file(VersioningPlugin.VERSION_FILE_NAME) << "version=1.0.0"

        expect:
        project.plugins.apply("org.mockito.mockito-release-tools.continuous-delivery")
    }
}
