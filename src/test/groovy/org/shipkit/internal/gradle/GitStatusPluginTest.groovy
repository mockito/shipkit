package org.shipkit.internal.gradle

import org.shipkit.exec.ProcessRunner
import spock.lang.Specification

class GitStatusPluginTest extends Specification {

    def "git status invokes process just once"() {
        def runner = Mock(ProcessRunner.class)
        def status = new GitStatusPlugin.GitStatus(runner)

        when:
        status.branch
        status.branch

        then:
        1 * runner.run("git", "rev-parse", "--abbrev-ref", "HEAD") >> "master"
        status.branch == 'master'
    }
}
