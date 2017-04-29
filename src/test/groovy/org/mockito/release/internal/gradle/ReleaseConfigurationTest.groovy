package org.mockito.release.internal.gradle

import org.mockito.release.gradle.ReleaseConfiguration
import spock.lang.Specification

class ReleaseConfigurationTest extends Specification {

    def conf = new ReleaseConfiguration()

    def "custom commitMessagePostfix"() {
        //TODO figure out a test that would validate all properties with reflection
        //rather than implement individual unit test for each property (getter and setter)
        conf.git.commitMessagePostfix = " by CI build 1234 [ci skip-release]"

        expect:
        conf.git.commitMessagePostfix ==  " by CI build 1234 [ci skip-release]"
    }

    def "empty build settings are ok"() {
        expect:
        conf.build.commitMessage == null
        !conf.build.pullRequest
    }
}
