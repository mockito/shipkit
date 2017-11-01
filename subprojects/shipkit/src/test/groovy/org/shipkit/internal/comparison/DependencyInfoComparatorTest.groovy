package org.shipkit.internal.comparison

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class DependencyInfoComparatorTest extends Specification {

    @Rule
    TemporaryFolder tmp = new TemporaryFolder()

    def "compares dependency-info files correctly"(String leftContent, String rightContent, boolean expectedResult) {
        given:
        def dependencyInfoComparator = new DependencyInfoComparator()

        def leftFile = tmp.newFile("left")
        def rightFile = tmp.newFile("right")

        expect:
        dependencyInfoComparator.areEqual(leftFile, rightFile, leftContent, rightContent).areFilesEqual() == expectedResult

        where:
        leftContent    | rightContent          | expectedResult
        "leftContent"  | "rightParsedContent"  | false
        "sameContent"  | "sameContent"         | true
    }

    def "does not allow null previousSourcesJar"() {
        when:
        new DependencyInfoComparator().areEqual(null, new File(""), "", "")
        then:
        thrown(IllegalArgumentException)
    }

    def "does not allow null currentSourcesJar"() {
        when:
        new DependencyInfoComparator().areEqual(new File(""), null,  "", "")
        then:
        thrown(IllegalArgumentException)
    }

    def "does not allow null previousFileContent"() {
        when:
        new DependencyInfoComparator().areEqual(new File(""), new File(""), null, "")
        then:
        thrown(IllegalArgumentException)
    }

    def "does not allow null currentFileContent"() {
        when:
        new DependencyInfoComparator().areEqual(new File(""), new File(""), "", null)
        then:
        thrown(IllegalArgumentException)
    }
}
