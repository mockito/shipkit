package org.shipkit.internal.notes.vcs

import org.shipkit.internal.exec.ProcessRunner
import spock.lang.Specification

class GitOriginRepoProviderTest extends Specification {

    ProcessRunner runner
    GitOriginRepoProvider underTest

    void setup(){
        runner = Mock(ProcessRunner)
        underTest = new GitOriginRepoProvider(runner)
    }

    def "should return git remote correctly" (){
        given:
        runner.run(_) >> "git@github.com:mockito/mockito.git\n"

        expect:
        underTest.originGitRepo == "mockito/mockito"
    }
}
