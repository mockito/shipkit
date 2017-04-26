package org.mockito.release.internal.gradle.util

import org.mockito.release.gradle.ReleaseConfiguration
import spock.lang.Specification


class GitUtilTest extends Specification {

    def "getCommitMessage" () {
        def releaseConfigMock = Mock(ReleaseConfiguration)
        def gitMock = Mock(ReleaseConfiguration.Git)
        releaseConfigMock.git >> gitMock
        gitMock.commitMessagePostfix >> " [ci skip]"

        expect:
        GitUtil.getCommitMessage(releaseConfigMock, "some commit message") == "some commit message [ci skip]"
    }
}
