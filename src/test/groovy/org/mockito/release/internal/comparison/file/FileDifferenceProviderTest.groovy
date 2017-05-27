package org.mockito.release.internal.comparison.file

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.mockito.release.internal.gradle.BuildABTestingPlugin
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
        BuildABTestingPlugin.CompareResult result = new FileDifferenceProvider().getDifference(dirA, dirB);

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
        BuildABTestingPlugin.CompareResult result = new FileDifferenceProvider().getDifference(dirA, dirB);

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
        BuildABTestingPlugin.CompareResult result = new FileDifferenceProvider().getDifference(dirA, dirB);

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
        BuildABTestingPlugin.CompareResult result = new FileDifferenceProvider().getDifference(dirA, dirB);

        then:
        result.onlyA.isEmpty()
        result.onlyB == [dirZ.parentFile.parentFile, dirZ.parentFile, dirZ, fileW]
        result.bothButDifferent.isEmpty()
    }

    private void createSomeSameContent() {
        File dirAFile = new File(dirA, 'newFile')
        dirAFile << "someContent"
        File dirBFile = new File(dirB,'newFile')
        dirBFile << 'someContent'
    }
}
