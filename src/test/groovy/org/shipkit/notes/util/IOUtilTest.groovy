package org.shipkit.notes.util

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.shipkit.notes.util.IOUtil.readFully
import static org.shipkit.notes.util.IOUtil.writeFile

class IOUtilTest extends Specification {

    @Rule TemporaryFolder tmp = new TemporaryFolder()

    def "reads stream"() {
        expect:
        readFully(new ByteArrayInputStream("hey\njoe!".bytes)) == "hey\njoe!"
        readFully(new ByteArrayInputStream("\n".bytes)) == "\n"
        readFully(new ByteArrayInputStream("\n\n".bytes)) == "\n\n"
        readFully(new ByteArrayInputStream("".bytes)) == ""
    }

    def "closes streams"() {
        expect:
        IOUtil.close(null)
        IOUtil.close(new ByteArrayInputStream())
    }

    def "writes file"() {
        def f = new File(tmp.root, "x/y/z.txt")
        writeFile(f, "ala\nma")

        expect:
        readFully(f) == "ala\nma"
    }

    def "creates parent directories"() {
        when:
        IOUtil.createParentDirectory(new File(tmp.root.absolutePath + "/a/b/c"))
        IOUtil.createParentDirectory(new File(tmp.root.absolutePath + "/b"))
        then:
        new File(tmp.root.absolutePath + "/a/b").exists()
        !new File(tmp.root.absolutePath + "/a/b/c").exists()
        !new File(tmp.root.absolutePath + "/b").exists()
    }
}
