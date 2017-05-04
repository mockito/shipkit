package org.mockito.release.internal.comparison

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class VersionsComparatorTest extends Specification {

    @Rule
    TemporaryFolder tmp = new TemporaryFolder()

    VersionsComparator underTest = new VersionsComparator()

    def "downloads previousVersionFile, saves it locally and compares to currentVersionFile"() {
        given:
        underTest.projectGroup = "org.mockito"
        underTest.projectName = "api"
        underTest.previousVersion = "0.1.0"
        underTest.currentVersionFileLocalUrl = tmp.newFile("current-pom.xml")
        underTest.tempStorageDir = tmp.root
        underTest.extension = ".pom"

        def fileComparator = Mock(FileComparator)

        def remoteFile = tmp.newFile("remote")
        remoteFile << "fileContent"

        underTest.previousVersionFileRemoteUrl = "file://" + remoteFile.absolutePath
        underTest.fileComparator = fileComparator

        def expectedPreviousLocalUrl = tmp.newFile("api-0.1.0.pom")

        when:
        def result = underTest.compare()
        then:
        1 * fileComparator.areEqual(expectedPreviousLocalUrl, underTest.currentVersionFileLocalUrl) >> true

        assert result

        expectedPreviousLocalUrl.text == "fileContent"
    }
}
