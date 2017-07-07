package org.shipkit.internal.gradle.release.tasks

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class ComparisonResultsTest extends Specification {

    @Rule TemporaryFolder tmp = new TemporaryFolder()
    File diff
    File empty

    def setup() {
        diff = tmp.newFile(); diff << "diff"
        empty = tmp.newFile()
    }

    def "describes results"() {
        expect:
        new ComparisonResults([]).description ==
            "\n  Publication comparison was skipped (no comparison result files found)."
        new ComparisonResults([diff, empty]).description ==
            "\n  Compared 2 publication(s). Changes since previous release:\ndiff"
        new ComparisonResults([empty]).description ==
            "\n  Compared 1 publication(s). No changes since previous release!"
        new ComparisonResults([new File("does not exist")]).description ==
            "\n  Publication comparison was skipped (no comparison result files found)."
    }

    def "knows if results are identical"() {
        expect:
        !new ComparisonResults([]).areResultsIdentical()
        !new ComparisonResults([diff, empty]).areResultsIdentical()
        new ComparisonResults([empty]).areResultsIdentical()
        !new ComparisonResults([new File("does not exist")]).areResultsIdentical()
    }
}
