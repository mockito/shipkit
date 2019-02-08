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

    def "git add command for files"() {
        def f1 = new File("f1")
        def f2 = new File("f2")

        expect:
        getAddCommand([f1, f2], []) == ["git", "add", f1.absolutePath, f2.absolutePath]
    }

    def "git add command for directories"() {
        def d1 = new File("d1")
        def d2 = new File("d2")

        expect:
        getAddCommand([], [d1, d2]) == ["git", "add", d1.absolutePath, d2.absolutePath]
    }

    def "git add command for files and directories"() {
        def f1 = new File("f1")
        def d1 = new File("d1")

        expect:
        getAddCommand([f1], [d1]) == ["git", "add", f1.absolutePath, d1.absolutePath]
    }

    def "git commit command"() {
        expect:
        getCommitCommand("joe", "doe", ["desc"], "by shipkit") ==
            ["git", "commit", "--author", "joe <doe>", "-m", "desc by shipkit"]
    }
}
