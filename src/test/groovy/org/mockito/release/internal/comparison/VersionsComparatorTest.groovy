package org.mockito.release.internal.comparison

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class VersionsComparatorTest extends Specification {

    @Rule
    TemporaryFolder tmp = new TemporaryFolder()

    VersionsComparator underTest = new VersionsComparator()

    def "test"() {
        given:
        underTest.projectGroup = "org.mockito"
        underTest.projectName = "api"
        underTest.previousVersion = "0.1.0"
        underTest.currentVersionFileLocalUrl = tmp.newFile("current-pom.xml")
        underTest.tempStorageDir = tmp.root
        underTest.extension = ".pom"

        def fileComparator = Mock(FileComparator)
        def remoteUrlResolver = Mock(RemoteUrlResolver)

        def remoteFile = tmp.newFile("remote")
        remoteFile << "fileContent"

        remoteUrlResolver.resolveUrl(_,_,_,_) >> "file://" + remoteFile.absolutePath

        underTest.remoteUrlResolver = remoteUrlResolver
        underTest.fileComparator = fileComparator

        def expectedPreviousLocalUrl = tmp.newFile("api-0.1.0.pom")

        when:
        def result = underTest.compare()
        then:
        1 * fileComparator.areEqual(underTest.currentVersionFileLocalUrl, expectedPreviousLocalUrl) >> true

        assert result

        expectedPreviousLocalUrl.text == "fileContent"
    }
}
