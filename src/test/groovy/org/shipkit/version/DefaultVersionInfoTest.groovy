package org.shipkit.version

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class DefaultVersionInfoTest extends Specification {

    @Rule TemporaryFolder dir = new TemporaryFolder()

    def "does not support files without 'version' property"() {
        def f = dir.newFile() << "asdf"
        when: org.shipkit.version.DefaultVersionInfo.fromFile(f)
        then: thrown(IllegalArgumentException)
    }

    def "knows version stored in the file"() {
        def f = dir.newFile() << """
foo=bar
version=2.0.0
#version=3.0.0
x
"""
        expect:
        org.shipkit.version.DefaultVersionInfo.fromFile(f).version == "2.0.0"
    }

    def "increments version in file"() {
        def f = dir.newFile() << """
foo=bar
version=2.0.0
#version=3.0.0
x
"""

        when:
        def v = org.shipkit.version.DefaultVersionInfo.fromFile(f)
        def v2 = v.bumpVersion(false)

        then:
        f.text == """
foo=bar
version=2.0.1
#version=3.0.0
x
previousVersion=2.0.0
"""
        v.version == "2.0.0"
        v2.version == "2.0.1"
    }

    def "increments correctly even if no line break after version"() {
        def f = dir.newFile() << "foo=bar\nversion=2.0.0"

        when:
        org.shipkit.version.DefaultVersionInfo.fromFile(f).bumpVersion(false)

        then:
        f.text == "foo=bar\nversion=2.0.1\npreviousVersion=2.0.0\n"
    }

    def "knows notable versions"() {
        expect:
        def f = dir.newFile() << "version=1.0\n" + file
        org.shipkit.version.DefaultVersionInfo.fromFile(f).notableVersions.toString() == versions.toString()

        where:
        file                                       | versions
        "notableVersions= 1.0, 2.0-beta.1, 3.5.6 " | ['1.0', '2.0-beta.1', '3.5.6']
        "notableVersions="                         | []
        "foo="                                     | []
    }

    def "bumps notable version"() {
        def f = dir.newFile() << """
version=2.0.0
notableVersions=1.0.0
"""

        when:
        def v = org.shipkit.version.DefaultVersionInfo.fromFile(f)
        v.bumpVersion(true)

        then:
        f.text == """
version=2.0.1
notableVersions=2.0.0, 1.0.0
previousVersion=2.0.0
"""
        v.notableVersions.toString() == ["2.0.0", "1.0.0"].toString()
    }

    def "bumps notable version when no prior notable versions"() {
        def f = dir.newFile() << "version=1.0.0"

        when:
        def v = org.shipkit.version.DefaultVersionInfo.fromFile(f)
        v.bumpVersion(true)

        then:
        f.text == """version=1.0.1
notableVersions=1.0.0
previousVersion=1.0.0
"""
        v.notableVersions.toString() == ["1.0.0"].toString()
    }

    def "knows if version is a notable release"() {
        def f = dir.newFile() << "version=1.0.0"

        when:
        def v = org.shipkit.version.DefaultVersionInfo.fromFile(f)
        v.bumpVersion(true)

        then:
        f.text == """version=1.0.1
notableVersions=1.0.0
previousVersion=1.0.0
"""
        v.notableVersions.toString() == ["1.0.0"].toString()
    }

    def "bumps previous version"() {
        def f = dir.newFile() << """
version=2.0.0
previousVersion=1.0.1
"""

        when:
        def beforeBump = org.shipkit.version.DefaultVersionInfo.fromFile(f)
        def afterBump = beforeBump.bumpVersion(false)

        then:
        f.text == """
version=2.0.1
previousVersion=2.0.0
"""
        beforeBump.previousVersion == "1.0.1"
        afterBump.previousVersion == "2.0.0"
    }

    def "when bumping version sets previousVersion if it was null before"() {
        def f = dir.newFile() << """
version=1.0.0
"""

        when:
        def beforeBump = org.shipkit.version.DefaultVersionInfo.fromFile(f)
        def afterBump = beforeBump.bumpVersion(false)

        then:
        f.text == """
version=1.0.1
previousVersion=1.0.0
"""
        beforeBump.previousVersion == null
        afterBump.previousVersion == "1.0.0"
    }
}
