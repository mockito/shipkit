package org.shipkit.internal.comparison

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class DependencyInfoComparatorTest extends Specification {

    @Rule
    TemporaryFolder tmp = new TemporaryFolder()

    def "compares dependency-info files correctly"(String leftParsedContent, String rightParsedContent, boolean expectedResult) {
        given:
        def filter = Mock(DependencyInfoFilter)
        def dependencyInfoComparator = new DependencyInfoComparator(filter)

        def leftFile = tmp.newFile("left")
        def rightFile = tmp.newFile("right")

        def leftContent = "leftOriginalContent"
        def rightContent = "rightOriginalContent"

        leftFile << leftContent
        rightFile << rightContent

        filter.filter(leftContent) >> leftParsedContent
        filter.filter(rightContent) >> rightParsedContent

        expect:
        dependencyInfoComparator.areEqual(leftFile, rightFile, leftContent, rightContent).areFilesEqual() == expectedResult

        where:
        leftParsedContent | rightParsedContent    | expectedResult
        "leftContent"     | "rightParsedContent"  | false
        "sameContent"     | "sameContent"         | true
    }

    def "does not allow null projectGroup"() {
        when:
        new DependencyInfoComparator(null, "0.1", "0.2")
        then:
        thrown(IllegalArgumentException)
    }

    def "does not allow null previousVersion"() {
        when:
        new DependencyInfoComparator("org.mockito", null, "0.2")
        then:
        thrown(IllegalArgumentException)
    }

    def "does not allow null currentVersion"() {
        when:
        new DependencyInfoComparator("org.mockito", "0.1", null)
        then:
        thrown(IllegalArgumentException)
    }
}
