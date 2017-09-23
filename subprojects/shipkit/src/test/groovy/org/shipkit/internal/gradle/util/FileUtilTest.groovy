package org.shipkit.internal.gradle.util

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.shipkit.internal.notes.header.HeaderProvider
import spock.lang.Specification

class FileUtilTest extends Specification {

    @Rule TemporaryFolder tmp = new TemporaryFolder()
    HeaderProvider headerProvider = new HeaderProvider()

    def "appends to top"() {
        def f = tmp.newFile()
        f.text = headerProvider.getHeader("header")
        FileUtil.appendToTop(  'foo', f)

        expect:
        f.text == "foo"
    }

    def "appends to top even if file does not exist"() {
        def f = new File(tmp.newFolder(), "test")
        FileUtil.appendToTop("foo", f)

        expect:
        f.text == "foo"
    }

    def "finds files by pattern"(){
        def test = tmp.newFile("test.log")
        tmp.newFile("test.txt")
        tmp.newFolder("testDir")
        def test2 = tmp.newFile("testDir/test2.log")
        tmp.newFolder("testDir", "testDir2")
        def test3 = tmp.newFile("testDir/testDir2/test3.log")

        expect:
        FileUtil.findFilesByPattern(tmp.root.absolutePath, "**/**.log") as Set ==
            [test.absolutePath, test2.absolutePath, test3.absolutePath] as Set
    }
}
