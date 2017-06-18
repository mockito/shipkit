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

    def "should return git remote correctly for ssh config" (){
        given:
        runner.run(_) >> "git@github.com:mockito/mockito.git\n"

        expect:
        underTest.originGitRepo == "mockito/mockito"
    }

    def "should return git remote correctly for https config" (){
        given:
        runner.run(_) >> "https://github.com/mockito/mockito.git\n"

        expect:
        underTest.originGitRepo == "mockito/mockito"
    }
}
