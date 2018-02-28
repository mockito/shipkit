package org.shipkit.internal.gradle.util

import spock.lang.Specification

class BranchUtilsTest extends Specification {

    def "getHeadBranch ('#forkRepositoryName', '#headBranch')"() {
        expect:
        BranchUtils.getHeadBranch(forkRepositoryName, headBranch) == expected

        where:
        forkRepositoryName  | headBranch    | expected
        'mockito/shipkit'   | 'master'      | 'mockito:master'
        'mockito/mockito'   | '2.x'         | 'mockito:2.x'
        'epeee/shipkit'     | 'master'      | 'epeee:master'
    }
}
