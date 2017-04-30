package org.mockito.release.internal.comparison

import org.junit.Rule
import org.junit.rules.TemporaryFolder
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

    def "fails early when any of the zips cannot be opened"() {
        when: new ZipComparator().compareFiles(new File("foox"), new File("bar"))
        then:
        def ex = thrown(ZipComparator.ZipCompareException)
        ex.message.contains("foox")
    }

    private static boolean eq(File z1, File z2) {
        new ZipComparator().compareFiles(z1, z2) &&
                new ZipComparator().compareFiles(z2, z1)
    }
}
