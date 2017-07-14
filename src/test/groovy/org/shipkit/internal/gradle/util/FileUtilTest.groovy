package org.shipkit.internal.gradle.util

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class FileUtilTest extends Specification {

    @Rule TemporaryFolder tmp = new TemporaryFolder()

    def "appends to top"() {
        def f = tmp.newFile()
        FileUtil.appendToTop("foo", f)

        expect:
        f.text == "foo"
    }

    def "appends to top even if file does not exist"() {
        def f = new File(tmp.newFolder(), "test")
        FileUtil.appendToTop("foo", f)

        expect:
        f.text == "foo"
    }
}
