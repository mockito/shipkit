package org.shipkit.internal.comparison

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.shipkit.internal.comparison.diff.DirectoryDiffGenerator
import spock.lang.Specification
import testutil.ZipMaker

class ZipComparatorTest extends Specification {

    @Rule TemporaryFolder tmp = new TemporaryFolder()

    def "compares zips"() {
        ZipMaker zip = new ZipMaker(tmp.newFolder())

        File zip1 =             zip.newZip("1.txt", "1", "x/2.txt", "2", "x/y/3.txt", "3", "x/y/4.txt", "4")
        File zip2 =             zip.newZip("1.txt", "1", "x/2.txt", "2", "x/y/3.txt", "3", "x/y/4.txt", "4")
        File differentContent = zip.newZip("1.txt", "1", "x/2.txt", "2", "x/y/3.txt", "3", "x/y/4.txt", "XX")
        File missingFile      = zip.newZip("1.txt", "1", "x/2.txt", "2", "x/y/3.txt", "3")
        File extraFile        = zip.newZip("1.txt", "1", "x/2.txt", "2", "x/y/3.txt", "3", "x/y/4.txt", "4", "x.txt", "")

        expect:
        eq zip1, zip2

        !eq(zip1, differentContent)
        !eq(zip1, missingFile)
        !eq(zip1, extraFile)
    }

    def "ignores META-INF/dependency-info.md differences"() {
        ZipMaker zip = new ZipMaker(tmp.newFolder())

        File zip1 = zip.newZip("META-INF/dependency-info.md", "a")
        File zip2 = zip.newZip("META-INF/dependency-info.md", "b")

        expect:
        eq zip1, zip2
    }

    def "fails early when any of the zips cannot be opened"() {
        when:
        new ZipComparator().areEqual(new File("foox"), new File("bar"))
        then:
        def ex = thrown(RuntimeException)
        ex.message.contains("foox")
    }

    def "passes added/removed/modified files to diff generator"() {
        given:
        ZipMaker zip = new ZipMaker(tmp.newFolder())
        File zip1 = zip.newZip("1.txt", "1",
                               "2.txt", "2",
                               "3.txt", "3",
                               "4.txt", "4",
                               "5.txt", "5")

        File zip2 = zip.newZip("2.txt", "changed",
                               "3.txt", "3",
                               "4.txt", "4",
                               "6.txt", "added file")
        def directoryDiffGenerator = Mock(DirectoryDiffGenerator)

        def zipComparator = new ZipComparator(directoryDiffGenerator)
        when:
        zipComparator.areEqual(zip1, zip2)

        then:
        1 * directoryDiffGenerator.generateDiffOutput(["6.txt"], ["1.txt", "5.txt"], ["2.txt"])
    }

    private static boolean eq(File z1, File z2) {
        new ZipComparator().areEqual(z1, z2).areFilesEqual() &&
                new ZipComparator().areEqual(z2, z1).areFilesEqual()
    }
}
