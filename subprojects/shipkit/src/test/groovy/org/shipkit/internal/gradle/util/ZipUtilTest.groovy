package org.shipkit.internal.gradle.util

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import testutil.ZipMaker

class ZipUtilTest extends Specification {

    @Rule
    TemporaryFolder tmp = new TemporaryFolder()

    File file

    void setup() {
        def zip = new ZipMaker(tmp.newFolder())
        file = zip.newZip("1.txt", "1", "2.txt", "2")
    }

    def "fileContainsEntry works correctly"() {
        expect:
        ZipUtil.fileContainsEntry(file, "1.txt")
        ZipUtil.fileContainsEntry(file, "2.txt")
        !ZipUtil.fileContainsEntry(file, "3.txt")
    }

    def "extractEntries works correctly"() {
        expect:
        ZipUtil.extractEntries(ZipUtil.openZipFile(file)) == ["1.txt", "2.txt"] as Set
    }

    def "readEntryContent works correctly"() {
        expect:
        ZipUtil.readEntryContent(file, "1.txt") == "1"
    }
}
