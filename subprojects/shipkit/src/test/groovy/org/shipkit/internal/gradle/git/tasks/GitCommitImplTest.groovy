package org.shipkit.internal.gradle.git.tasks

import spock.lang.Specification

import static org.shipkit.internal.gradle.git.tasks.GitCommitImpl.getAddCommand
import static org.shipkit.internal.gradle.git.tasks.GitCommitImpl.getAggregatedCommitMessage
import static org.shipkit.internal.gradle.git.tasks.GitCommitImpl.getCommitCommand

class GitCommitImplTest extends Specification {

    def "aggregates message correctly"() {
        expect:
        getAggregatedCommitMessage([]) == ""
        getAggregatedCommitMessage(["release notes updated", "version bumped"]) == "release notes updated + version bumped"
    }

    def "git add command"() {
        def f1 = new File("f1")
        def f2 = new File("f2")

        expect:
        getAddCommand([f1, f2]) == ["git", "add", f1.absolutePath, f2.absolutePath]
    }

    def "git commit command"() {
        expect:
        getCommitCommand("joe", "doe", ["desc"], "by shipkit") ==
            ["git", "commit", "--author", "joe <doe>", "-m", "desc by shipkit"]
    }
}
