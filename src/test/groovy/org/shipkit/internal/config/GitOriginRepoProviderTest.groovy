package org.shipkit.internal.config

import org.shipkit.internal.exec.ProcessRunner
import spock.lang.Specification

class GitOriginRepoProviderTest extends Specification {

    ProcessRunner runner
    GitOriginRepoProvider underTest

    void setup(){
        runner = Mock(ProcessRunner)
        underTest = new GitOriginRepoProvider(runner)
    }

    def "should return fallback value if git remote throws any exception" (){
        given:
        runner.run(_) >> { throw new RuntimeException()}

        expect:
        underTest.originGitRepo == "mockito/mockito-release-tools-example"
    }

    def "should return git remote correctly" (){
        given:
        runner.run(_) >> "git@github.com:mockito/mockito.git\n"

        expect:
        underTest.originGitRepo == "mockito/mockito"
    }
}
