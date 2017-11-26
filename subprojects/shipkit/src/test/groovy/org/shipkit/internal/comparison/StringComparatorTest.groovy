package org.shipkit.internal.comparison

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class StringComparatorTest extends Specification {

    @Rule
    TemporaryFolder tmp = new TemporaryFolder()

    def "compares strings correctly"(String leftContent, String rightContent, boolean expectedResult) {
        given:
        def stringComparator = new StringComparator()

        expect:
        stringComparator.areEqual(leftContent, rightContent).areFilesEqual() == expectedResult

        where:
        leftContent    | rightContent          | expectedResult
        "leftContent"  | "rightParsedContent"  | false
        "sameContent"  | "sameContent"         | true
    }

    def "does not allow null previousContent"() {
        when:
        new StringComparator().areEqual(null, "")
        then:
        thrown(IllegalArgumentException)
    }

    def "does not allow null currentContent"() {
        when:
        new StringComparator().areEqual("", null)
        then:
        thrown(IllegalArgumentException)
    }
}
