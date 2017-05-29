package org.shipkit.internal.comparison

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class PomComparatorTest extends Specification {

    @Rule
    TemporaryFolder tmp = new TemporaryFolder()

    def "compares poms correctly"(String leftParsedContent, String rightParsedContent, boolean expectedResult) {
        given:
        PomFilter remover = Mock(PomFilter)
        PomComparator pomComparator = new PomComparator(remover)

        def leftFile = tmp.newFile("left")
        def rightFile = tmp.newFile("right")

        def leftContent = "leftOriginalContent"
        def rightContent = "rightOriginalContent"

        leftFile << leftContent
        rightFile << rightContent

        remover.filter(leftContent) >> leftParsedContent
        remover.filter(rightContent) >> rightParsedContent

        expect:
        pomComparator.areEqual(leftFile, rightFile) == expectedResult

        where:
        leftParsedContent | rightParsedContent    | expectedResult
        "leftContent"     | "rightParsedContent"  | false
        "sameContent"     | "sameContent"         | true
    }

    def "does not allow null projectGroup"() {
        when:
        new PomComparator(null, "0.1", "0.2")
        then:
        thrown(IllegalArgumentException)
    }

    def "does not allow null previousVersion"() {
        when:
        new PomComparator("org.mockito", null, "0.2")
        then:
        thrown(IllegalArgumentException)
    }

    def "does not allow null currentVersion"() {
        when:
        new PomComparator("org.mockito", "0.1", null)
        then:
        thrown(IllegalArgumentException)
    }
}
