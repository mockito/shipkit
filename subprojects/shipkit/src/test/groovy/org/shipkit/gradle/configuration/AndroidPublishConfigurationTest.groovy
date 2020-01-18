package org.shipkit.gradle.configuration

import org.gradle.api.GradleException
import spock.lang.Specification

class AndroidPublishConfigurationTest extends Specification {

    def conf = new AndroidPublishConfiguration();

    def "throws when artifact id not configured"() {
        when:
        conf.artifactId

        then:
        thrown(GradleException)
    }

    def "stores artifact id"() {
        when:
        conf.artifactId = "org.shipkit.android"

        then:
        conf.artifactId == "org.shipkit.android"
    }
}
