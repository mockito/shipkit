package org.mockito.release.internal.comparison.file

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification


class FileDifferenceProviderTest extends Specification {

    @Rule
    TemporaryFolder tmp = new TemporaryFolder()

    File dirA, dirB

    def setup() {
        dirA = tmp.newFolder('dirA')
        dirB = tmp.newFolder('dirB')
    }

    def "sameContent" () {
        given:
        createSomeSameContent()

        when:
        CompareResult result = new FileDifferenceProvider().getDifference(dirA, dirB);

        then:
        result.onlyA.isEmpty()
        result.onlyB.isEmpty()
        result.bothButDifferent.isEmpty()
    }

    def "onlyA" () {
        given:
        createSomeSameContent()
        File dirC = new File(dirA, 'b/c')
        dirC.mkdirs()
        File fileD = new File(dirC, 'd')
        fileD << 'content of d'

        when:
        CompareResult result = new FileDifferenceProvider().getDifference(dirA, dirB);

        then:
        result.onlyA == [dirC.parentFile, dirC, fileD]
        result.onlyB.isEmpty()
        result.bothButDifferent.isEmpty()
    }

    def "another onlyA" () {
        given:
        createSomeSameContent()
        File dirC = new File(dirA, 'b/c')
        dirC.mkdirs()
        File fileD = new File(dirC, 'd')
        fileD << 'content of d'
        File dirT = new File(dirA, 't')
        dirT.mkdirs()
        File fileU = new File(dirT, 'u')
        fileU << 'content of u'

        when:
        CompareResult result = new FileDifferenceProvider().getDifference(dirA, dirB);

        then:
        result.onlyA == [dirC.parentFile, dirC, fileD, dirT, fileU]
        result.onlyB.isEmpty()
        result.bothButDifferent.isEmpty()
    }

    def "onlyB" () {
        given:
        createSomeSameContent()
        File dirZ = new File(dirB, 'x/y/z')
        dirZ.mkdirs()
        File fileW = new File(dirZ, 'w')
        fileW << 'content of d'

        when:
        CompareResult result = new FileDifferenceProvider().getDifference(dirA, dirB);

        then:
        result.onlyA.isEmpty()
        result.onlyB == [dirZ.parentFile.parentFile, dirZ.parentFile, dirZ, fileW]
        result.bothButDifferent.isEmpty()
    }

    def "both but different"() {
        given:
        createSomeSameContent()

        File dirADifferentFile = new File(dirA, 'different')
        dirADifferentFile << "someContent"
        File dirBDifferentFile = new File(dirB,'different')
        dirBDifferentFile << 'differentContent'

        when:
        CompareResult result = new FileDifferenceProvider().getDifference(dirA, dirB);

        then:
        result.onlyA.isEmpty()
        result.onlyB.isEmpty()
        result.bothButDifferent == [dirADifferentFile, dirBDifferentFile]
    }

    def "both but different (same length)"() {
        given:
        createSomeSameContent()

        File dirADifferentFile = new File(dirA, 'different')
        dirADifferentFile << "content A"
        File dirBDifferentFile = new File(dirB,'different')
        dirBDifferentFile << 'content B'

        when:
        CompareResult result = new FileDifferenceProvider().getDifference(dirA, dirB);

        then:
        result.onlyA.isEmpty()
        result.onlyB.isEmpty()
        result.bothButDifferent == [dirADifferentFile, dirBDifferentFile]
    }

    def "same files and same content"() {
        given:
        createSomeSameContent()

        when:
        CompareResult result = new FileDifferenceProvider().getDifference(dirA, dirB);

        then:
        result.onlyA.isEmpty()
        result.onlyB.isEmpty()
        result.bothButDifferent.isEmpty()
    }

    private void createSomeSameContent() {
        File dirAFile = new File(dirA, 'newFile')
        dirAFile << "someContent"
        File dirBFile = new File(dirB,'newFile')
        dirBFile << 'someContent'
    }

}
